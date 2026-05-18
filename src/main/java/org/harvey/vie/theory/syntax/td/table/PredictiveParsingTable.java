package org.harvey.vie.theory.syntax.td.table;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 19:00
 */
public interface PredictiveParsingTable {
    int EPSILON_REFERENCE = -1;
    int END_MARK_REFERENCE = 0;
    TerminalSymbol END_MARK_SYMBOL = TerminalSymbol.END_MARK_SYMBOL;

    /**
     * @return null/{@link GrammarSymbol#EPSILON}/{@link GrammarConcatenation}
     */
    AlterableSymbol get(HeadSymbol head, SourceToken token);

    GrammarUnitSymbol terminalStart(TerminalFactor factor);

    GrammarUnitSymbol headStart(String definition);
}
