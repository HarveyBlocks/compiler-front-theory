package org.harvey.vie.theory.syntax.bu.table.element;

import lombok.AllArgsConstructor;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 23:37
 */
@AllArgsConstructor
public class ShiftTableElementImpl implements ShiftTableElement {
    private final int nextStatus;

    @Override
    public int nextStatus() {
        return nextStatus;
    }

    @Override
    public String toString() {
        return "shift " + nextStatus;
    }
}
