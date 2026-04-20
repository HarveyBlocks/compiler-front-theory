package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.CommandNodeBuilder;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:13
 */
@AllArgsConstructor
public class NormalCommandNodeRegister implements CommandContext.CommandNodeRegister {
    private final CommandContext.CommandNode[] childrenNode;
    private final SimpleGrammarProduction production;

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new CommandContext.HeadNode(childrenNode, production));
    }
}
