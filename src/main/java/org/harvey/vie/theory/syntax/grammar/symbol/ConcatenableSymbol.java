package org.harvey.vie.theory.syntax.grammar.symbol;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 01:00
 */
public interface ConcatenableSymbol extends GrammarSymbol {

    @Override
    default boolean isEpsilon() {
        return false;
    }

    @Override
    default boolean isConcatenable() {
        return true;
    }

    @Override
    default ConcatenableSymbol toConcatenable() {
        return this;
    }
}

