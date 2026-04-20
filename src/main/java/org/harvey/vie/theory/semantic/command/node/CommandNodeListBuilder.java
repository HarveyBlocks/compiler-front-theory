package org.harvey.vie.theory.semantic.command.node;

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
    private final List<CommandNode> list = new ArrayList<>();

    @Override
    public void add(CommandNode node) {
        list.add(node);
    }

    public CommandNode[] toArray() {
        return list.toArray(CommandNode[]::new);
    }
}
