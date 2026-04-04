package org.harvey.vie.theory.syntax.bu.item;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 23:41
 */
@AllArgsConstructor
public class ProductionItemImpl implements ProductionItem {
    private final ProductionSetContext context;
    private final int productionIndex;
    private final int alternationIndex;
    private final int currentDot;


    @Override
    public int currentDot() {
        return currentDot;
    }

    @Override
    public HeadSymbol getHead() {
        return context.get(productionIndex).getHead();
    }

    @Override
    public AlterableSymbol getAlterableSymbol() {
        return context.get(productionIndex).getBody().get(alternationIndex);
    }

    @Override
    public boolean hasNextSymbol() {
        AlterableSymbol alterableSymbol = getAlterableSymbol();
        if (alterableSymbol.isEpsilon()) {
            return false;
        }
        return currentDot < alterableSymbol.toConcatenation().size();
    }

    @Override
    public GrammarUnitSymbol nextSymbol() {
        AlterableSymbol alterableSymbol = getAlterableSymbol();
        if (alterableSymbol.isEpsilon()) {
            throw new IllegalStateException("epsilon alterable symbol do not has next symbol");
        }
        return alterableSymbol.toConcatenation().get(currentDot);
    }

    @Override
    public ProductionItem nextItem() {
        if (!hasNextSymbol()) {
            throw new IllegalStateException("next item is not exist!");
        }
        return new ProductionItemImpl(context, productionIndex, alternationIndex, currentDot + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductionItemImpl)) {
            return false;
        }
        ProductionItemImpl that = (ProductionItemImpl) o;
        return currentDot == that.currentDot &&
               productionIndex == that.productionIndex &&
               alternationIndex == that.alternationIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentDot, productionIndex, alternationIndex);
    }

    @Override
    public String toString() {
        StringJoiner itemJoiner = new StringJoiner(" ");
        HeadSymbol head = getHead();
        AlterableSymbol alterableSymbol = getAlterableSymbol();
        if (alterableSymbol.isEpsilon()) {
            itemJoiner.add(alterableSymbol.toString());
        } else {
            int cur = currentDot;
            for (GrammarUnitSymbol unitSymbol : alterableSymbol.toConcatenation()) {
                if (cur == 0) {
                    itemJoiner.add("·");
                }
                itemJoiner.add(unitSymbol.toString());
                cur--;
            }
            if (cur == 0) {
                itemJoiner.add("·");
            }
        }
        return head + " -> " + itemJoiner;
    }
}
