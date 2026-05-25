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

    private SequnceStep(T item, ShiftReduceSyntaxTreeNode tail) {
        this.item = item;
        this.tail = tail;
    }

    public static <T> SequnceStep<T> item(T item, ShiftReduceSyntaxTreeNode tail) {
        return new SequnceStep<>(item, tail);
    }

    public static <T> SequnceStep<T> advance(ShiftReduceSyntaxTreeNode tail) {
        return new SequnceStep<>(null, tail);
    }

    public static <T> SequnceStep<T> stop() {
        return new SequnceStep<>(null, null);
    }

    boolean hasItem() {
        return item != null;
    }

    T item() {
        return item;
    }

    ShiftReduceSyntaxTreeNode tail() {
        return tail;
    }
}
