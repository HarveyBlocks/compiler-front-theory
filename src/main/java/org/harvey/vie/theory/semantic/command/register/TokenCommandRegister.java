package org.harvey.vie.theory.semantic.command.register;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;

import java.util.List;

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
    private final List<UncertainLabelGotoCommand> uncertainBreaks;
    private final List<UncertainLabelGotoCommand> uncertainContinues;

    public TokenCommandRegister(SemanticCommand command) {
        this(command, List.of(), List.of());
    }

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new TerminalNode(command));
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return uncertainBreaks;
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainContinues() {
        return uncertainContinues;
    }
}
