package org.harvey.vie.theory.semantic.command.command;

/**
 * 中间命令的统一标记接口。
 * <p>
 * 当前项目的命令不是 JVM 字节码，而是可打印的中间表示对象。演示实现主要是
 * {@link org.harvey.vie.theory.semantic.command.command.string.StringCommand} 和
 * {@link org.harvey.vie.theory.semantic.command.command.string.StringSupplierCommand}：
 * 前者保存固定文本，后者在打印时读取标签下标。
 * <p>
 * 讲解主线继续看命令工厂
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:25
 */
public interface SemanticCommand {
}
