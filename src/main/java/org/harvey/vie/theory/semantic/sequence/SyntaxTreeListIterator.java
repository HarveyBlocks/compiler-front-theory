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

    public SyntaxTreeListIterator(ShiftReduceSyntaxTreeNode start, Stepper<T> stepper) {
        this.cursor = start;
        this.stepper = Objects.requireNonNull(stepper);
        this.nextItem = advance();
    }

    @Override
    public boolean hasNext() {
        return nextItem != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more list items.");
        }
        T current = nextItem;
        nextItem = advance();
        return current;
    }

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
