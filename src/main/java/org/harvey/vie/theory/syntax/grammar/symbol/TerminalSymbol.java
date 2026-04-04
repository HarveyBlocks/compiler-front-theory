package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:45
 */
public interface TerminalSymbol extends GrammarUnitSymbol {

    TerminalFactor getFactor();

    @Override
    default boolean isTerminal() {
        return true;
    }

    @Override
    default TerminalSymbol toTerminal() {
        return this;
    }

    boolean match(SourceToken token);

    String hint();
}


