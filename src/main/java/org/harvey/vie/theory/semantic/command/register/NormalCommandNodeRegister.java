package org.harvey.vie.theory.semantic.command.register;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.HeadNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:13
 */
@AllArgsConstructor
public class NormalCommandNodeRegister implements CommandNodeRegister {
    private final CommandNode[] childrenNode;
    private final SimpleGrammarProduction production;

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new HeadNode(childrenNode, production));
    }
}
