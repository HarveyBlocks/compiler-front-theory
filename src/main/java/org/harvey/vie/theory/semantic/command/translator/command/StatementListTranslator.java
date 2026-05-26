package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
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
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length == 0) {
            return new PlaceholderNodeRegister();
        }
        if (children.length == 1) {
            return children[0];
        }
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        for (CommandNodeRegister child : children) {
            child.register(thisBuilder);
        }
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}
