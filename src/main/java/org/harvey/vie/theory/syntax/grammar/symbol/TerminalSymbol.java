package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.io.ILoader;
import org.harvey.vie.theory.io.Storage;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:45
 */
public interface TerminalSymbol extends GrammarUnitSymbol, Storage {
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

    interface Loader<T extends TerminalSymbol> extends ILoader<T> {
    }
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

    @Override
    public int store(OutputStream os) throws IOException {
        throw new UnsupportedOperationException("Do not store end mark terminal. " +
                                                "Leave it to the outside world to decide " +
                                                "how to persist and better deal with special situations");
    }

    static class Factor implements TerminalFactor {
        @Override
        public String toString() {
            return "$";
        }
    }

}
