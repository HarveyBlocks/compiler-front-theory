package org.harvey.vie.theory.semantic.sequence;

import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

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
    private ShiftReduceSyntaxTreeNode cursor;
    private SequnceStep<T> nextStep;

    public SyntaxTreeListIterator(ShiftReduceSyntaxTreeNode start, Stepper<T> stepper) {
        this.cursor = start;
        this.stepper = Objects.requireNonNull(stepper);
        this.nextStep = advance();
    }

    @Override
    public boolean hasNext() {
        return nextStep != null && nextStep.hasItem();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more list items.");
        }
        T current = nextStep.item();
        cursor = nextStep.tail();
        nextStep = advance();
        return current;
    }

    private SequnceStep<T> advance() {
        while (cursor != null) {
            if (!cursor.isHead()) {
                cursor = null;
                return null;
            }
            SequnceStep<T> step = stepper.step(cursor.toHead());
            if (step == null) {
                cursor = null;
                return null;
            }
            if (step.hasItem()) {
                return step;
            }
            cursor = step.tail();
        }
        return null;
    }

}
