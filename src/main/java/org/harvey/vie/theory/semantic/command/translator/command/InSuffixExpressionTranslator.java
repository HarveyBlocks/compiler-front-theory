package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
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
        validateOperandTypes(context);
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder);
        children[2].register(thisBuilder);
        thisBuilder.add(new TerminalNode(CommandFactory.stOperator(operatorFactor)));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }

    private void validateOperandTypes(ShiftReduceSemanticContext context) {
        org.harvey.vie.theory.semantic.tree.node.HeadNode top = SemanticTypeResolver.topReducedNode(context);
        if (top == null || top.size() != 3) {
            return;
        }
        SemanticType left = SemanticTypeResolver.resolve(context, top.get(0));
        SemanticType right = SemanticTypeResolver.resolve(context, top.get(2));
        SourceToken operatorToken = top.get(1).toToken().getSource();
        ProgramTokenType operatorType = (ProgramTokenType) operatorToken.getType();
        if (operatorType == ProgramTokenType.OPERATOR_LOGICAL_AND ||
            operatorType == ProgramTokenType.OPERATOR_LOGICAL_OR) {
            if (!left.isUnknown() && !left.isBooleanScalar() || !right.isUnknown() && !right.isBooleanScalar()) {
                reject(context, operatorToken, "logical operator requires boolean operands.");
            }
            return;
        }
        if (operatorType == ProgramTokenType.OPERATOR_PLUS ||
            operatorType == ProgramTokenType.OPERATOR_MINUS ||
            operatorType == ProgramTokenType.OPERATOR_MULTIPLY ||
            operatorType == ProgramTokenType.OPERATOR_DIVIDE) {
            if (!left.isUnknown() && !left.isNumericScalar() || !right.isUnknown() && !right.isNumericScalar()) {
                reject(context, operatorToken, "arithmetic operator requires numeric operands.");
            }
            return;
        }
        if (operatorType == ProgramTokenType.OPERATOR_LESS ||
            operatorType == ProgramTokenType.OPERATOR_LESS_EQUAL ||
            operatorType == ProgramTokenType.OPERATOR_GREATER ||
            operatorType == ProgramTokenType.OPERATOR_GREATER_EQUAL) {
            if (!left.isUnknown() && !left.isNumericScalar() || !right.isUnknown() && !right.isNumericScalar()) {
                reject(context, operatorToken, "relational operator requires numeric operands.");
            }
            return;
        }
        if (operatorType == ProgramTokenType.OPERATOR_EQUAL ||
            operatorType == ProgramTokenType.OPERATOR_NOT_EQUAL) {
            if (!left.isUnknown() && !right.isUnknown() && !left.equals(right)) {
                reject(context, operatorToken, "equality operator requires operands of the same type.");
            }
        }
    }

    private void reject(ShiftReduceSemanticContext context, SourceToken operatorToken, String message) {
        context.addError(operatorToken.getOffset(), message);
        throw new CompilerException(message);
    }
}
