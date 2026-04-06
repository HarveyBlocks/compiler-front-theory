package org.harvey.vie.theory.syntax.bu.table;

import org.harvey.vie.theory.syntax.bu.item.ItemSet;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:39
 */
public interface ShiftReduceParsingTable {
    int NONE = ItemSet.NONE;

    int gotoNext(int originStatus, HeadSymbol head);

    ActiveTableElement activeNext(int originStatus, TerminalSymbol terminal);
}
