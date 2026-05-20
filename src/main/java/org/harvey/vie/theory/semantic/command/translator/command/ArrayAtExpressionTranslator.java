package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
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
 * TODO lvalue->lvalue [ expr ]
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class ArrayAtExpressionTranslator implements CommandTranslator {

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production, CommandNodeRegister[] children) {
        // lvalue.command();
        // expr.command();
        // CommandFactory.bias_from_st_top_to_ref();
        if (children.length != 4) {
            throw new CompilerException("illegal statement on array at expression production.");
        }
        validateIndexType(context);
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder); // lvalue
        children[2].register(thisBuilder); // expr
        thisBuilder.add(new TerminalNode(CommandFactory.biasFromStTopToRef()));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }

    private void validateIndexType(ShiftReduceSemanticContext context) {
        org.harvey.vie.theory.semantic.tree.node.HeadNode top = SemanticTypeResolver.topReducedNode(context);
        if (top == null || top.size() != 4) {
            return;
        }
        SemanticType baseType = SemanticTypeResolver.resolve(context, top.get(0));
        SemanticType indexType = SemanticTypeResolver.resolve(context, top.get(2));
        SourceToken bracketToken = top.get(1).toToken().getSource();
        if (!baseType.isUnknown() && !baseType.isArray()) {
            reject(context, bracketToken, "subscript operator requires an array operand.");
        }
        if (!indexType.isUnknown() && !SemanticType.scalar(SemanticType.Kind.INT32).equals(indexType)) {
            reject(context, bracketToken, "array index must be int32.");
        }
    }

    private void reject(ShiftReduceSemanticContext context, SourceToken token, String message) {
        context.addError(token.getOffset(), message);
        throw new CompilerException(message);
    }
}
