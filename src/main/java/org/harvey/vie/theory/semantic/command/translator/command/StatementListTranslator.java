package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:22
 */
public class StatementListTranslator implements CommandTranslator {
    @Override
    public CommandContext.CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandContext.CommandNodeRegister[] children) {
        if (children.length != 2) {
            throw new CompilerException("illegal statement on statement list production.");
        }
        CommandNodeListBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder);
        children[1].register(thisBuilder);
        return new NormalCommandNodeRegister(thisBuilder.toArray(), production);
    }
}
