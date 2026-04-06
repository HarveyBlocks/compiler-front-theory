package org.harvey.vie.theory.syntax.bu.dr;

import org.harvey.vie.theory.syntax.bu.item.ProductionItem;
import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 20:39
 */
public interface DeRemerSetFactory {
    Map<HeadSymbol, Set<TerminalSymbol>> produce(Iterable<ProductionItem> set, FirstMap firstMap);
}
