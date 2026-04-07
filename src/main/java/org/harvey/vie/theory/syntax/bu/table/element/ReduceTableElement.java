package org.harvey.vie.theory.syntax.bu.table.element;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:45
 */
public interface ReduceTableElement extends ActiveTableElement {

    @Override
    default boolean conflict(ActiveTableElement other) {
        if (!other.isReduce() || other.isAccept()) {
            return true;
        }
        return Objects.equals(getProduction(), other.getProduction());
    }

    @Override
    default boolean isShift() {
        return false;
    }

    @Override
    default boolean isReduce() {
        return true;
    }

    @Override
    default boolean isAccept() {
        return false;
    }
}
