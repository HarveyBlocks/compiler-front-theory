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

    private SequnceStep(T item, ShiftReduceSyntaxTreeNode tail, boolean deferred) {
        this.item = item;
        this.tail = tail;
        this.deferred = deferred;
    }

    public static <T> SequnceStep<T> item(T item, ShiftReduceSyntaxTreeNode tail) {
        return new SequnceStep<>(item, tail, false);
    }

    public static <T> SequnceStep<T> defer(T item, ShiftReduceSyntaxTreeNode tail) {
        return new SequnceStep<>(item, tail, true);
    }

    public static <T> SequnceStep<T> advance(ShiftReduceSyntaxTreeNode tail) {
        return new SequnceStep<>(null, tail, false);
    }

    public static <T> SequnceStep<T> stop() {
        return new SequnceStep<>(null, null, false);
    }

    boolean hasItem() {
        return item != null;
    }

    boolean isDeferred() {
        return deferred;
    }

    T item() {
        return item;
    }

    ShiftReduceSyntaxTreeNode tail() {
        return tail;
    }
}
