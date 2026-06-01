package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;

/**
 * 占位注册器：表示某个语法片段没有运行时中间命令。
 * <p>
 * 类型、分号、空列表、纯声明等都可能走这里。它的 {@link #register(CommandNodeBuilder)}
 * 什么也不做，因此不会影响最终命令顺序。
 * <p>
 * 讲完本类回到 {@link CommandNodeRegister}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:31
 */
public class PlaceholderNodeRegister implements CommandNodeRegister {
    public PlaceholderNodeRegister() {
    }

    @Override
    public void register(CommandNodeBuilder outer) {
        // do nothing
    }
}
