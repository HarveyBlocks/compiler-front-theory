package org.harvey.vie.theory.syntax.bu.table;

import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:45
 */
public interface ReduceTableElement extends ActiveTableElement {
    HeadSymbol getHead();

    AlterableSymbol getBody();

    @Override
    default boolean conflict(ActiveTableElement other) {
        if (!other.isReduce() || other.isAccept()) {
            return true;
        }
        return Objects.equals(getHead(), other.getHead()) && Objects.equals(getBody(), other.getBody());
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
