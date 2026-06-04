package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
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
 * 翻译单目表达式，如逻辑非和算术取负。
 * <p>
 * 如果该表达式已经在常量传播阶段折叠为常量，就直接复用常量装载逻辑；
 * 否则先生成操作数求值代码，再补上对应的一元运算指令。
 *
 * @author Temper
 */
public class UnaryExpressionTranslator implements CommandTranslator {
    private final OperatorFactor operatorFactor;
    private final ProgramTokenType operatorType;
/**
 * 函数功能：创建 UnaryExpressionTranslator 对象。
 * 输入：
 * - operatorFactor：OperatorFactor 类型参数。
 * - operatorType：ProgramTokenType 类型参数。
 * 输出：无。
 */

    public UnaryExpressionTranslator(OperatorFactor operatorFactor, ProgramTokenType operatorType) {
        this.operatorFactor = operatorFactor;
        this.operatorType = operatorType;
    }
/**
 * 函数功能：翻译语法节点并返回命令节点注册器。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - production：SimpleGrammarProduction 类型参数。
 * - children：CommandNodeRegister[] 类型参数。
 * 输出：CommandNodeRegister 类型返回值。
 */

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        CommandNodeRegister constant = ConstantCommandSupport.constantOrNull(context, production, children);
        if (constant != null) {
            return constant;
        }
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[1].register(thisBuilder);
        SemanticType operandType = TypeAttributes.childType(context, 1);
        SemanticType instructionType;
        if (operatorType == ProgramTokenType.OPERATOR_LOGICAL_NOT) {
            SemanticDiagnostics.requireBoolean(
                    context,
                    operandType,
                    TypeAttributes.childAnchor(context, 0),
                    "operator '!' requires a boolean operand."
            );
            instructionType = SemanticType.scalar(SemanticType.Kind.BOOLEAN);
        } else if (operatorType == ProgramTokenType.OPERATOR_MINUS) {
            SemanticDiagnostics.requireNumeric(
                    context,
                    operandType,
                    TypeAttributes.childAnchor(context, 0),
                    "unary '-' requires a numeric operand."
            );
            instructionType = operandType;
        } else {
            throw new IllegalStateException("unsupported unary operator type: " + operatorType);
        }
        thisBuilder.add(new TerminalNode(context.getCommandFactory().stOperator(
                operatorFactor,
                CommandDataType.forValue(instructionType)
        )));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}

