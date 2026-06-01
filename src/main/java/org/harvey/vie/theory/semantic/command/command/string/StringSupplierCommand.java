package org.harvey.vie.theory.semantic.command.command.string;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;

import java.util.function.Supplier;

/**
 * 延迟求值文本命令。
 * <p>
 * 控制流跳转命令需要等 {@link org.harvey.vie.theory.semantic.command.node.LabelNode} 展开后才能知道目标下标，
 * 所以不能在创建命令时就固定字符串。本类保存一个 {@link Supplier}，
 * {@link #toString()} 被 {@link org.harvey.vie.theory.semantic.command.ThreeAddressCodePrinter} 调用时，
 * 再读取最新标签下标并生成文本。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:24
 */
@AllArgsConstructor
public class StringSupplierCommand implements SemanticCommand {
    private final Supplier<String> stringSupplier;

    @Override
    public String toString() {
        return stringSupplier.get();
    }
}
