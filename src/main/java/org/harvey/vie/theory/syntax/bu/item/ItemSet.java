package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.util.SimpleList;

import java.util.Map;
import java.util.Set;

/**
 * TODO 项集
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 22:13
 */
public interface ItemSet extends SimpleList<ProductionItem> {
    int NONE = -1;

    boolean contains(ProductionItem item);


    /**
     * @return {@link #NONE} for none, else >= 0
     */
    int gotoUnit(GrammarUnitSymbol unit);

    Set<TerminalSymbol> decisionRule(HeadSymbol head);


    Map<HeadSymbol, Set<TerminalSymbol>> getDecisionRule();

    Map<HeadSymbol, Integer> getHeadGoto();

    Map<TerminalSymbol, Integer> getTerminalGoto();
}
