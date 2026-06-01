package org.harvey.vie.theory.semantic.command.command;

import lombok.Data;

/**
 * 控制流标签的简单实现，只保存最终线性命令下标。
 * <p>
 * {@link org.harvey.vie.theory.semantic.command.node.LabelNode} 展开时调用 {@link #setIndex(int)}；
 * {@link org.harvey.vie.theory.semantic.command.command.string.StringSupplierCommand} 打印跳转命令时读取
 * {@link #getIndex()}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:33
 */
@Data
public class DefaultSemanticLabel implements SemanticLabel {
    private int index;
}
