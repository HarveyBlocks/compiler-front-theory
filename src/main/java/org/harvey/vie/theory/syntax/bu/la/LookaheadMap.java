package org.harvey.vie.theory.syntax.bu.la;

import org.harvey.vie.theory.syntax.bu.item.ItemSet;
import org.harvey.vie.theory.syntax.bu.item.ProductionItem;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Set;

/**
 * TODO 一个 {@link ItemSet} 对应一个Lookahead
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 15:07
 */
public interface LookaheadMap {

    boolean contains(ProductionItem item, TerminalSymbol terminalSymbol);

    Set<TerminalSymbol> get(ProductionItem item);
}
