package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.analysis.SemanticTypeDiagnostics;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class InSuffixExpressionTranslator implements CommandTranslator {
    private final OperatorFactor operatorFactor;

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production, CommandNodeRegister[] children) {
        // 中缀表达式, 是处理形如 expr -> expr operator item 的产生式
        // expr.command();
        // term.command();
        // CommandFactory.st_operator();
        if (children.length != 3) {
            throw new CompilerException("illegal statement on in-suffix expression production.");
        }
        SemanticType leftType = TypeAttributes.childType(context, 0);
        SemanticType rightType = TypeAttributes.childType(context, 2);
        SourceToken operatorToken = TypeAttributes.childAnchor(context, 1);
        SemanticType instructionType = inferInstructionType(context, leftType, rightType, operatorToken);
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder);
        if (context.requiresImplicitCast(leftType, instructionType)) {
            // TODO 你的意思是, 现在直接把类型转换耦合放到Translator里面了吗?
            thisBuilder.add(new TerminalNode(CommandFactory.stTopCast(leftType, instructionType)));
        }
        children[2].register(thisBuilder);
        if (context.requiresImplicitCast(rightType, instructionType)) {
            thisBuilder.add(new TerminalNode(CommandFactory.stTopCast(rightType, instructionType)));
        }
        thisBuilder.add(new TerminalNode(CommandFactory.stOperator(operatorFactor, instructionType)));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }

    private SemanticType inferInstructionType(
            ShiftReduceSemanticContext context,
            SemanticType leftType,
            SemanticType rightType,
            SourceToken token) {
        String operator = operatorFactor.toString();
        // TODO 为什么有解析字符串?
        if ("logical_or".equals(operator) || "logical_and".equals(operator)) {
            SemanticTypeDiagnostics.requireBoolean(context, leftType, token, "logical operator requires boolean operands.");
            SemanticTypeDiagnostics.requireBoolean(context, rightType, token, "logical operator requires boolean operands.");
            return SemanticType.scalar(SemanticType.Kind.BOOLEAN);
        }
        // TODO 为什么有解析字符串?
        if ("equal".equals(operator) || "not_equal".equals(operator)) {
            boolean sameType = leftType.equals(rightType);
            boolean numericComparable = leftType.isNumericScalar() && rightType.isNumericScalar();
            if (!sameType && !numericComparable) {
                SemanticTypeDiagnostics.reject(context, token, "equality operator requires identical types or comparable numeric types.");
            }
            return numericComparable ? context.commonBinaryType(leftType, rightType) : leftType;
        }
        // TODO 为什么有解析字符串?
        if ("less".equals(operator) || "less_equal".equals(operator) ||
            "greater".equals(operator) || "greater_equal".equals(operator)) {
            SemanticTypeDiagnostics.requireNumeric(context, leftType, token, "relational operator requires numeric operands.");
            SemanticTypeDiagnostics.requireNumeric(context, rightType, token, "relational operator requires numeric operands.");
            return context.commonBinaryType(leftType, rightType);
        }
        SemanticTypeDiagnostics.requireNumeric(context, leftType, token, "arithmetic operator requires numeric operands.");
        SemanticTypeDiagnostics.requireNumeric(context, rightType, token, "arithmetic operator requires numeric operands.");
        return context.commonBinaryType(leftType, rightType);
    }
}
