package org.harvey.vie.theory.semantic.command.node;

/**
 * 命令节点收集器接口。
 * <p>
 * 各个 {@link org.harvey.vie.theory.semantic.command.register.CommandNodeRegister} 通过
 * {@link #add(CommandNode)} 把自己的节点写入外层翻译器。最终 {@link #build()} 交给
 * {@link org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister} 形成一个非终结节点。
 * 当前实现是 {@link CommandNodeListBuilder}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 23:02
 */
public interface CommandNodeBuilder {
    void add(CommandNode node);

    CommandNode[] build();
}
