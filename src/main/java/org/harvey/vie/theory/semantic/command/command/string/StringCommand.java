package org.harvey.vie.theory.semantic.command.command.string;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

/**
 * 固定文本命令：当前 demo 的 {@link SemanticCommand} 主要实现之一。
 * <p>
 * 它直接保存一行中间代码文本，{@link #toString()} 返回该文本。也就是说最终
 * {@link org.harvey.vie.theory.semantic.command.ThreeAddressCodePrinter} 打印出的命令，
 * 本质上就是这些对象的 {@code toString()}。
 * <p>
 * 跳转命令因为标签下标要后解析，使用 {@link StringSupplierCommand}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:26
 */
@AllArgsConstructor
public class StringCommand implements SemanticCommand {
    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
