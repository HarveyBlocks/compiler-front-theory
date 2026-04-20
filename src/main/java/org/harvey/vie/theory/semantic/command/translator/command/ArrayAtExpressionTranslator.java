package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
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
    public CommandContext.CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production, CommandContext.CommandNodeRegister[] children) {
        // lvalue.command();
        // expr.command();
        // CommandFactory.bias_from_st_top_to_ref();
        if (children.length != 2) {
            throw new CompilerException("illegal statement on array at expression production.");
        }
        CommandNodeListBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder); // lvalue
        children[1].register(thisBuilder); // expr
        thisBuilder.add(new CommandContext.TerminalNode(CommandFactory.biasFromStTopToRef()));
        return new NormalCommandNodeRegister(thisBuilder.toArray(), production);
    }
}
