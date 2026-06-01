package org.harvey.vie.theory.semantic.command.command;

/**
 * 控制流标签接口。
 * <p>
 * 翻译器先创建标签对象并交给跳转命令；真正的命令下标要等
 * {@link org.harvey.vie.theory.semantic.command.node.LabelNode#flat(java.util.List)} 线性展开时才能确定。
 * 这就是跳转命令能先生成、后解析目标位置的原因。
 * <p>
 * 默认实现是 {@link DefaultSemanticLabel}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:32
 */
public interface SemanticLabel {
    void setIndex(int index);

    int getIndex();
}
