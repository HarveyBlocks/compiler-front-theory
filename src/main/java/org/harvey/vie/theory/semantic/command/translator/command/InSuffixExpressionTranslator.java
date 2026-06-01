package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 表达式支路：翻译二元中缀表达式，例如 {@code a + b}、{@code a < b}、{@code x && y}。
 * <p>
 * 这里的命令模型是“先把左操作数求到栈顶，再把右操作数求到栈顶，最后发出一个栈式运算命令”。
 * 虽然底层命令文本带有 {@code st_} 栈顶操作风格，但它仍是中间代码文本，不是 JVM 字节码。
 * 运算符由 {@link OperatorFactor} 描述，类型规则按 {@link OperatorCategory} 分成逻辑、相等、关系、算术四类。
 * <p>
 * 如果 {@link ConstantCommandSupport} 已经发现当前表达式是编译期常量，本类直接返回常量装载命令；
 * 否则会根据左右类型推断运算指令类型，必要时插入
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#stTopCast(CommandDataType, CommandDataType)}。
 * 讲完表达式支路可看 {@link UnaryExpressionTranslator}，或回到
 * {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
 *
 * @author Temper
 */
@AllArgsConstructor
public class InSuffixExpressionTranslator implements CommandTranslator {
    private final OperatorFactor operatorFactor;

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        CommandNodeRegister constant = ConstantCommandSupport.constantOrNull(context, production, children);
        if (constant != null) {
            return constant;
        }
        SemanticType leftType = TypeAttributes.childType(context, 0);
        SemanticType rightType = TypeAttributes.childType(context, 2);
        SourceToken operatorToken = TypeAttributes.childAnchor(context, 1);
        SemanticType instructionType = inferInstructionType(context, leftType, rightType, operatorToken);
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder);
        if (context.requiresImplicitCast(leftType, instructionType)) {
            // 左操作数先入栈，如果它不是本次运算采用的公共类型，就立刻转换栈顶。
            thisBuilder.add(new TerminalNode(context.getCommandFactory().stTopCast(
                    CommandDataType.forValue(leftType),
                    CommandDataType.forValue(instructionType)
            )));
        }
        children[2].register(thisBuilder);
        if (context.requiresImplicitCast(rightType, instructionType)) {
            // 右操作数同理，运算命令执行前保证两个操作数的命令数据类型一致。
            thisBuilder.add(new TerminalNode(context.getCommandFactory().stTopCast(
                    CommandDataType.forValue(rightType),
                    CommandDataType.forValue(instructionType)
            )));
        }
        thisBuilder.add(new TerminalNode(context.getCommandFactory().stOperator(
                operatorFactor,
                CommandDataType.forValue(instructionType)
        )));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }

    private SemanticType inferInstructionType(
            ShiftReduceSemanticContext context,
            SemanticType leftType,
            SemanticType rightType,
            SourceToken token) {
        if (operatorFactor.isLogical()) {
            return logicalInstructionType(context, leftType, rightType, token);
        }
        if (operatorFactor.isEquality()) {
            return equalityInstructionType(context, leftType, rightType, token);
        }
        if (operatorFactor.isRelational()) {
            return relationalInstructionType(context, leftType, rightType, token);
        }
        if (operatorFactor.isArithmetic()) {
            return arithmeticInstructionType(context, leftType, rightType, token);
        }
        throw new IllegalStateException("unsupported infix operator: " + operatorFactor.mnemonic());
    }

    private SemanticType logicalInstructionType(
            ShiftReduceSemanticContext context,
            SemanticType leftType,
            SemanticType rightType,
            SourceToken token) {
        SemanticDiagnostics.requireBoolean(context, leftType, token, "logical operator requires boolean operands.");
        SemanticDiagnostics.requireBoolean(context, rightType, token, "logical operator requires boolean operands.");
        return SemanticType.scalar(SemanticType.Kind.BOOLEAN);
    }

    private SemanticType equalityInstructionType(
            ShiftReduceSemanticContext context,
            SemanticType leftType,
            SemanticType rightType,
            SourceToken token) {
        boolean sameType = leftType.equals(rightType);
        boolean numericComparable = leftType.isNumericScalar() && rightType.isNumericScalar();
        if (!sameType && !numericComparable) {
            SemanticDiagnostics.reject(
                    context,
                    token,
                    "equality operator requires identical types or comparable numeric types."
            );
        }
        return numericComparable ? context.commonBinaryType(leftType, rightType) : leftType;
    }

    private SemanticType relationalInstructionType(
            ShiftReduceSemanticContext context,
            SemanticType leftType,
            SemanticType rightType,
            SourceToken token) {
        SemanticDiagnostics.requireNumeric(context, leftType, token, "relational operator requires numeric operands.");
        SemanticDiagnostics.requireNumeric(context, rightType, token, "relational operator requires numeric operands.");
        return context.commonBinaryType(leftType, rightType);
    }

    private SemanticType arithmeticInstructionType(
            ShiftReduceSemanticContext context,
            SemanticType leftType,
            SemanticType rightType,
            SourceToken token) {
        SemanticDiagnostics.requireNumeric(context, leftType, token, "arithmetic operator requires numeric operands.");
        SemanticDiagnostics.requireNumeric(context, rightType, token, "arithmetic operator requires numeric operands.");
        return context.commonBinaryType(leftType, rightType);
    }
}
