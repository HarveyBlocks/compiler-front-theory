package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.util.SimpleList;

import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:03
 */
public interface FirstMap extends SimpleList<Map.Entry<GrammarUnitSymbol, FirstSet>> {
    FirstSet get(HeadSymbol head);

    FirstSet get(TerminalSymbol terminal);

    Set<TerminalSymbol> terminalSet();
    Set<HeadSymbol> headSet();

    FirstSet first(Iterable<GrammarUnitSymbol> iterable);

    boolean nullable(Iterable<GrammarUnitSymbol> iterable);
}
