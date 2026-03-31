package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO 产生式中的选择
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:49
 */
public interface GrammarAlternation extends GrammarSymbol, Iterable<GrammarSymbol> {
    void alternate(GrammarSymbol symbol);

    void set(int i,  GrammarConcatenation concatenation);
    GrammarSymbol get(int i);

    int size();

    boolean isEmpty();

    void alternateEpsilon();

    boolean alternatedEpsilon();

    @Override
    default boolean isEpsilon() {
        return false;
    }

    @Override
    default boolean isConcatenable() {
        return false;
    }

}
