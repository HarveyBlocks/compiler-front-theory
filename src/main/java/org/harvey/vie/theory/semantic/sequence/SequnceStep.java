package org.harvey.vie.theory.semantic.sequence;

import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 13:59
 */
public final class SequnceStep<T> {
    private final T item;
    private final ShiftReduceSyntaxTreeNode tail;
    private final boolean deferred;
/**
 * 函数功能：创建 SequnceStep 对象。
 * 输入：
 * - item：T 类型参数。
 * - tail：ShiftReduceSyntaxTreeNode 类型参数。
 * - deferred：boolean 类型参数。
 * 输出：无。
 */

    private SequnceStep(T item, ShiftReduceSyntaxTreeNode tail, boolean deferred) {
        this.item = item;
        this.tail = tail;
        this.deferred = deferred;
    }
/**
 * 函数功能：创建包含元素的序列步骤。
 * 输入：
 * - item：T 类型参数。
 * - tail：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：SequnceStep<T> 类型返回值。
 */

    public static <T> SequnceStep<T> item(T item, ShiftReduceSyntaxTreeNode tail) {
        return new SequnceStep<>(item, tail, false);
    }
/**
 * 函数功能：创建延迟序列步骤。
 * 输入：
 * - item：T 类型参数。
 * - tail：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：SequnceStep<T> 类型返回值。
 */

    public static <T> SequnceStep<T> defer(T item, ShiftReduceSyntaxTreeNode tail) {
        return new SequnceStep<>(item, tail, true);
    }
/**
 * 函数功能：推进到下一个序列状态。
 * 输入：
 * - tail：ShiftReduceSyntaxTreeNode 类型参数。
 * 输出：SequnceStep<T> 类型返回值。
 */

    public static <T> SequnceStep<T> advance(ShiftReduceSyntaxTreeNode tail) {
        return new SequnceStep<>(null, tail, false);
    }
/**
 * 函数功能：创建停止序列步骤。
 * 输入：
 * - 无。
 * 输出：SequnceStep<T> 类型返回值。
 */

    public static <T> SequnceStep<T> stop() {
        return new SequnceStep<>(null, null, false);
    }
/**
 * 函数功能：判断序列步骤是否包含元素。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    boolean hasItem() {
        return item != null;
    }
/**
 * 函数功能：判断序列步骤是否延迟。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    boolean isDeferred() {
        return deferred;
    }

    T item() {
        return item;
    }
/**
 * 函数功能：获取尾部序列步骤。
 * 输入：
 * - 无。
 * 输出：ShiftReduceSyntaxTreeNode 类型返回值。
 */

    ShiftReduceSyntaxTreeNode tail() {
        return tail;
    }
}
