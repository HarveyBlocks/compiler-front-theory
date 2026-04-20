package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.CommandNodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 23:01
 */
public class CommandNodeListBuilder implements CommandNodeBuilder {
    private final List<CommandContext.CommandNode> list = new ArrayList<>();

    @Override
    public void add(CommandContext.CommandNode node) {
        list.add(node);
    }

    public CommandContext.CommandNode[] toArray() {
        return list.toArray(CommandContext.CommandNode[]::new);
    }
}
