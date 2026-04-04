package org.harvey.vie.theory.syntax.td.table;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 19:00
 */
public interface AnalysisTable {
    int EPSILON_REFERENCE = -1;
    int END_MARK_REFERENCE = 0;
    TerminalSymbol END_MARK_SYMBOL = new EndMarkTerminal();

    /**
     * @return null/{@link GrammarSymbol#EPSILON}/{@link GrammarConcatenation}
     */
    AlterableSymbol get(HeadSymbol head, SourceToken token);

    GrammarUnitSymbol terminalStart(TerminalFactor factor);
    GrammarUnitSymbol headStart(String definition);
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