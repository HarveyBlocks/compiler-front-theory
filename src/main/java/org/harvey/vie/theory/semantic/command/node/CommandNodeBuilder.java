package org.harvey.vie.theory.semantic.command.node;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 23:02
 */
public interface CommandNodeBuilder {
    /**
     * 函数功能：添加指定元素。
     * 输入：
     * - node：CommandNode 类型参数。
     * 输出：无。
     */
    void add(CommandNode node);

    /**
     * 函数功能：构建目标对象。
     * 输入：
     * - 无。
     * 输出：CommandNode[] 类型数组。
     */

    CommandNode[] build();
}
