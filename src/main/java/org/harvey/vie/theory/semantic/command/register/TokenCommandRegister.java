package org.harvey.vie.theory.semantic.command.register;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:22
 */
@AllArgsConstructor
public class TokenCommandRegister implements CommandNodeRegister {
    private final SemanticCommand command;

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new TerminalNode(command));
    }
}
