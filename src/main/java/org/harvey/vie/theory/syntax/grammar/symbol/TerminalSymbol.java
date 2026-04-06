package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:45
 */
public interface TerminalSymbol extends GrammarUnitSymbol {
    TerminalSymbol END_MARK_SYMBOL = new EndMarkTerminal();

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


class EndMarkTerminal implements TerminalSymbol {
    public static final TerminalFactor FACTOR = new Factor();

    @Override
    public TerminalFactor getFactor() {
        return FACTOR;
    }

    @Override
    public boolean match(SourceToken token) {
        return token == SourceTokenIterator.NO_MORE_TOKEN;
    }

    @Override
    public String hint() {
        return "$";
    }

    @Override
    public String toString() {
        return hint();
    }

    static class Factor implements TerminalFactor {
        @Override
        public String toString() {
            return "$";
        }
    }

}
