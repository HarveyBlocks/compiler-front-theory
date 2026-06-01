package org.harvey.vie.theory.semantic.command.node;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于 {@link List} 的命令节点收集器。
 * <p>
 * 它只维护插入顺序，不做优化或重排。这个简单约束很重要：最终的中间代码顺序必须和语法制导翻译规则
 * 插入节点的顺序一致。讲完本类回到 {@link CommandNodeBuilder} 或
 * {@link org.harvey.vie.theory.semantic.command.CommandSegmentSupport}。
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

    @Override
    public CommandNode[] build() {
        return list.toArray(CommandNode[]::new);
    }
}
