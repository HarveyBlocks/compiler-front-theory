package org.harvey.vie.theory.syntax.bu.table.element;

import org.harvey.vie.theory.io.Storage;

import java.io.IOException;
import java.io.OutputStream;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:45
 */
public interface ActiveTableElement {
    boolean conflict(ActiveTableElement other);

    default int getProduction() {
        throw new UnsupportedOperationException("Do not support to invoke get production by this object");
    }


    default int nextStatus() {
        throw new UnsupportedOperationException("Do not support to invoke get production by this object");
    }

    boolean isShift();

    boolean isReduce();

    boolean isAccept();

    default void dealConflict(ActiveTableElement element) {
        throw new UnsupportedOperationException("Can not deal conflict!");
    }

}
