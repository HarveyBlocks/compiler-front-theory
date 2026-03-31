package org.harvey.vie.theory.syntax.td.first;

import org.harvey.vie.theory.syntax.grammar.symbol.ConcatenableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Iterator;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:03
 */
public interface FirstMap extends Iterable<Map.Entry<ConcatenableSymbol, FirstSet>> {
    FirstSet get(HeadSymbol head);

    FirstSet get(TerminalSymbol terminal);
}
