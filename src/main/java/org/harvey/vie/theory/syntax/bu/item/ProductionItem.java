package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

/**
 * TODO 产生式的项
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 22:14
 */
public interface ProductionItem {
    /**
     * @return currentDot [0,{@link #getAlterableSymbol}.size()]. 0 is before all
     */
    int currentDot();

    HeadSymbol getHead();

    AlterableSymbol getAlterableSymbol();

    boolean productionEquals(ProductionItem o);

    boolean hasNextSymbol();

    boolean hasPreviousSymbol();

    GrammarUnitSymbol nextSymbol();

    GrammarUnitSymbol previousSymbol();

    ProductionItem nextItem();

    Iterable<GrammarUnitSymbol> afterIterable();

    boolean isEpsilon();


}
