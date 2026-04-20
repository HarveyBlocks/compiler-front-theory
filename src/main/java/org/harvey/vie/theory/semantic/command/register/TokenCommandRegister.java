package org.harvey.vie.theory.semantic.command.register;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:22
 */
@AllArgsConstructor
public class TokenCommandRegister implements  CommandContext.CommandNodeRegister {
    private final SemanticCommand command;

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new CommandContext.TerminalNode(command));
    }
}
