package org.harvey.vie.theory.semantic.sequence;

import org.harvey.vie.theory.semantic.tree.node.HeadNode;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 14:00
 */
@FunctionalInterface
public interface Stepper<T> {
    /**
     * 函数功能：推进并返回当前序列步骤。
     * 输入：
     * - head：HeadNode 类型参数。
     * 输出：SequnceStep<T> 类型返回值。
     */
    SequnceStep<T> step(HeadNode head);
}
