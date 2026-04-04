package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 23:53
 */
public interface GrammarUnitSymbol extends ConcatenableSymbol {

    @Override
    default boolean isConcatenation() {
        return false;
    }

    @Override
    default GrammarUnitSymbol toUnit() {
        return this;
    }
}
