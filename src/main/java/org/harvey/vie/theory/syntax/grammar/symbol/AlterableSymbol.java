package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 19:54
 */
public interface AlterableSymbol extends GrammarSymbol {
    @Override
    default boolean isEpsilon() {
        return false;
    }
    @Override
    default boolean isAlterable() {
        return true;
    }



    @Override
    default AlterableSymbol toAlterable() {
        return this;
    }
}
