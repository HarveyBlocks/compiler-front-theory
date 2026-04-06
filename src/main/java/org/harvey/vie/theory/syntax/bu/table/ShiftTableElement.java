package org.harvey.vie.theory.syntax.bu.table;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:45
 */
public interface ShiftTableElement extends ActiveTableElement {
    int nextStatus();
    @Override
    default boolean conflict(ActiveTableElement other) {
        if ( !other.isShift()) {
            return false;
        } else if (other.nextStatus() != nextStatus()) {
            throw new IllegalStateException(
                    "For the same state I and the same terminal a, the result of GOTO(I, a) is uniquely determined, " +
                    "and thus it cannot be assigned two different shift targets.");
        }
        return true;
    }

    @Override
    default boolean isShift() {
        return true;
    }

    @Override
    default boolean isReduce() {
        return false;
    }

    @Override
    default boolean isAccept() {
        return false;
    }
}
