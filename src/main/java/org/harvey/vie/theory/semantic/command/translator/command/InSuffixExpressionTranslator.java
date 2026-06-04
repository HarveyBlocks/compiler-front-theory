package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * @author Temper
 */
@AllArgsConstructor
public class InSuffixExpressionTranslator implements CommandTranslator {
    private final OperatorFactor operatorFactor;

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
        SemanticType leftType = TypeAttributes.childType(context, 0);
        SemanticType rightType = TypeAttributes.childType(context, 2);
        SourceToken operatorToken = TypeAttributes.childAnchor(context, 1);
        SemanticType instructionType = inferInstructionType(context, leftType, rightType, operatorToken);
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder);
        if (context.requiresImplicitCast(leftType, instructionType)) {
            thisBuilder.add(new TerminalNode(context.getCommandFactory().stTopCast(
                    CommandDataType.forValue(leftType),
                    CommandDataType.forValue(instructionType)
            )));
        }
        children[2].register(thisBuilder);
        if (context.requiresImplicitCast(rightType, instructionType)) {
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

    /**
     * 函数功能：推断运算指令类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - leftType：SemanticType 类型参数。
     * - rightType：SemanticType 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：SemanticType 类型返回值。
     */

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

    /**
     * 函数功能：推断逻辑运算指令类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - leftType：SemanticType 类型参数。
     * - rightType：SemanticType 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：SemanticType 类型返回值。
     */

    private SemanticType logicalInstructionType(
            ShiftReduceSemanticContext context,
            SemanticType leftType,
            SemanticType rightType,
            SourceToken token) {
        SemanticDiagnostics.requireBoolean(context, leftType, token, "logical operator requires boolean operands.");
        SemanticDiagnostics.requireBoolean(context, rightType, token, "logical operator requires boolean operands.");
        return SemanticType.scalar(SemanticType.Kind.BOOLEAN);
    }

    /**
     * 函数功能：推断相等性运算指令类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - leftType：SemanticType 类型参数。
     * - rightType：SemanticType 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：SemanticType 类型返回值。
     */

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

    /**
     * 函数功能：推断关系运算指令类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - leftType：SemanticType 类型参数。
     * - rightType：SemanticType 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：SemanticType 类型返回值。
     */

    private SemanticType relationalInstructionType(
            ShiftReduceSemanticContext context,
            SemanticType leftType,
            SemanticType rightType,
            SourceToken token) {
        SemanticDiagnostics.requireNumeric(context, leftType, token, "relational operator requires numeric operands.");
        SemanticDiagnostics.requireNumeric(context, rightType, token, "relational operator requires numeric operands.");
        return context.commonBinaryType(leftType, rightType);
    }

    /**
     * 函数功能：推断算术运算指令类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - leftType：SemanticType 类型参数。
     * - rightType：SemanticType 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：SemanticType 类型返回值。
     */

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

