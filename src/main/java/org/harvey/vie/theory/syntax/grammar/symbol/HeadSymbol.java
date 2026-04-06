package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 01:19
 */
public interface HeadSymbol extends GrammarUnitSymbol {
    @Override
    default boolean isTerminal() {
        return false;
    }

    @Override
    default HeadSymbol toHead() {
        return this;
    }

    default boolean isDefine() {
        throw GrammarSymbol.unsupportedTest();
    }

    default HeadDefineSymbol toDefine() {
        throw GrammarSymbol.unsupportedCast();
    }
}
