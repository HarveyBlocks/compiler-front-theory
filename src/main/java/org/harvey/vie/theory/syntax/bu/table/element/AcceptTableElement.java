package org.harvey.vie.theory.syntax.bu.table.element;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:45
 */
public interface AcceptTableElement extends ReduceTableElement {

    @Override
    default boolean isAccept() {
        return true;
    }

    @Override
    default boolean conflict(ActiveTableElement other) {
        if (!other.isAccept()) {
            return true;
        }
        return Objects.equals(getProduction(), other.getProduction());
    }
}
