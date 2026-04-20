package org.harvey.vie.theory.semantic.command.node;

import lombok.Getter;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.util.IRandomAccess;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:29
 */
@Getter
public class TerminalNode extends IRandomAccess.EmptyImpl<CommandNode> implements
        CommandNode {
    private final SemanticCommand command;

    public TerminalNode(SemanticCommand command) {
        this.command = command;
    }

    @Override
    public void flat(List<SemanticCommand> result) {
        result.add(command);
    }
}
