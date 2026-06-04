package org.harvey.vie.theory.semantic.sequence;

import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Generic iterator for list-shaped syntax trees. Each {@link #next()} call
 * replaces the currently held node with the next list tail supplied by a
 * caller-provided stepping rule.
 *
 * @author Temper
 */
public final class SyntaxTreeListIterator<T> implements Iterator<T> {
    private final Stepper<T> stepper;
    private final ArrayDeque<T> deferredItems = new ArrayDeque<>();
    private ShiftReduceSyntaxTreeNode cursor;
    private T nextItem;
/**
 * 函数功能：创建 SyntaxTreeListIterator 对象。
 * 输入：
 * - start：ShiftReduceSyntaxTreeNode 类型参数。
 * - stepper：Stepper<T> 类型参数。
 * 输出：无。
 */

    public SyntaxTreeListIterator(ShiftReduceSyntaxTreeNode start, Stepper<T> stepper) {
        this.cursor = start;
        this.stepper = Objects.requireNonNull(stepper);
        this.nextItem = advance();
    }
/**
 * 函数功能：判断是否存在下一个元素。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean hasNext() {
        return nextItem != null;
    }
/**
 * 函数功能：获取下一个元素。
 * 输入：
 * - 无。
 * 输出：T 类型返回值。
 */

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more list items.");
        }
        T current = nextItem;
        nextItem = advance();
        return current;
    }
/**
 * 函数功能：推进到下一个序列状态。
 * 输入：
 * - 无。
 * 输出：T 类型返回值。
 */

    private T advance() {
        while (true) {
            while (cursor != null) {
                if (!cursor.isHead()) {
                    cursor = null;
                    continue;
                }
                SequnceStep<T> step = stepper.step(cursor.toHead());
                if (step == null) {
                    cursor = null;
                    continue;
                }
                cursor = step.tail();
                if (!step.hasItem()) {
                    continue;
                }
                if (step.isDeferred()) {
                    deferredItems.push(step.item());
                    continue;
                }
                return step.item();
            }
            if (deferredItems.isEmpty()) {
                return null;
            }
            return deferredItems.pop();
        }
    }

}
