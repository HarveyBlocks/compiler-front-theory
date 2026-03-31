package org.harvey.vie.theory.syntax.td.first;

import org.harvey.vie.theory.syntax.grammar.symbol.ConcatenableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.follow.FollowSetFactoryImpl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

    Set<TerminalSymbol> terminalSet();

    FirstSet first(Iterable<ConcatenableSymbol> iterable);
}
