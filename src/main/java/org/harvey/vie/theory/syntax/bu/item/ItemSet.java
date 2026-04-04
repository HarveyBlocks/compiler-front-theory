package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;

/**
 * TODO 项集
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 22:13
 */
public interface ItemSet extends Iterable<ProductionItem> {
    boolean contains(ProductionItem item);

    int size();

    int gotoUnit(GrammarUnitSymbol unit);
}
