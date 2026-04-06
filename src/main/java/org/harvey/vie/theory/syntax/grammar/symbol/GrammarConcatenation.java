package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.util.IRandomAccess;

/**
 * TODO 产生式中的连接
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:49
 */
public interface GrammarConcatenation extends ConcatenableSymbol, AlterableSymbol, IRandomAccess<GrammarUnitSymbol> {

    void concatenate(ConcatenableSymbol concatenable);


    @Override
    default boolean isEpsilon() {
        return false;
    }

    @Override
    default boolean isTerminal() {
        return false;
    }


    @Override
    default boolean isConcatenation() {
        return true;
    }

    @Override
    default GrammarConcatenation toConcatenation() {
        return this;
    }

}

