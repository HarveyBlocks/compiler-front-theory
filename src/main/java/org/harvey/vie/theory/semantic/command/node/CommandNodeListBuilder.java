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

    /**
     * 函数功能：添加指定元素。
     * 输入：
     * - node：CommandNode 类型参数。
     * 输出：无。
     */

    @Override
    public void add(CommandNode node) {
        list.add(node);
    }

    /**
     * 函数功能：构建目标对象。
     * 输入：
     * - 无。
     * 输出：CommandNode[] 类型数组。
     */

    @Override
    public CommandNode[] build() {
        return list.toArray(CommandNode[]::new);
    }
}
