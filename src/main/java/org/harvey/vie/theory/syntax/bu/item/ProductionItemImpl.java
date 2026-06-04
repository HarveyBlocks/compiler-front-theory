package org.harvey.vie.theory.syntax.bu.item;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.util.AfterIterable;

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

    /**
     * 函数功能：获取当前点的位置。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */


    @Override
    public int currentDot() {
        return currentDot;
    }

    /**
     * 函数功能：获取产生式头部符号。
     * 输入：
     * - 无。
     * 输出：HeadSymbol 类型返回值。
     */

    @Override
    public HeadSymbol getHead() {
        return context.get(productionIndex).getHead();
    }

    /**
     * 函数功能：获取可候选语法符号。
     * 输入：
     * - 无。
     * 输出：AlterableSymbol 类型返回值。
     */

    @Override
    public AlterableSymbol getAlterableSymbol() {
        return context.get(productionIndex).getBody().get(alternationIndex);
    }

    /**
     * 函数功能：判断两个项目是否属于同一产生式。
     * 输入：
     * - o：ProductionItem 类型参数。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean productionEquals(ProductionItem o) {
        if (getHead() != o.getHead()) {
            return false;
        }
        AlterableSymbol thisAlterable = getAlterableSymbol();
        AlterableSymbol thatAlterable = o.getAlterableSymbol();
        boolean thisEpsilon = thisAlterable.isEpsilon();
        boolean thatEpsilon = thatAlterable.isEpsilon();
        if (thisEpsilon && thatEpsilon) {
            return true;
        } else if (thisEpsilon != thatEpsilon) {
            return false;
        }
        GrammarConcatenation thisConcatenation = thisAlterable.toConcatenation();
        GrammarConcatenation thatConcatenation = thatAlterable.toConcatenation();
        return thisConcatenation.equals(thatConcatenation);
    }

    /**
     * 函数功能：判断点后是否存在语法符号。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean hasNextSymbol() {
        AlterableSymbol alterableSymbol = getAlterableSymbol();
        if (alterableSymbol.isEpsilon()) {
            return false;
        }
        return currentDot < alterableSymbol.toConcatenation().size();
    }

    /**
     * 函数功能：判断点前是否存在语法符号。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean hasPreviousSymbol() {
        if (isEpsilon()) {
            return false;
        }
        return currentDot > 0;
    }

    /**
     * 函数功能：获取点后的语法符号。
     * 输入：
     * - 无。
     * 输出：GrammarUnitSymbol 类型返回值。
     */

    @Override
    public GrammarUnitSymbol nextSymbol() {
        AlterableSymbol alterableSymbol = getAlterableSymbol();
        if (alterableSymbol.isEpsilon()) {
            throw new IllegalStateException("epsilon alterable symbol do not has next symbol");
        }
        return alterableSymbol.toConcatenation().get(currentDot);
    }

    /**
     * 函数功能：获取点前的语法符号。
     * 输入：
     * - 无。
     * 输出：GrammarUnitSymbol 类型返回值。
     */

    @Override
    public GrammarUnitSymbol previousSymbol() {
        AlterableSymbol alterableSymbol = getAlterableSymbol();
        if (alterableSymbol.isEpsilon()) {
            throw new IllegalStateException("epsilon alterable symbol do not has next symbol");
        }
        return alterableSymbol.toConcatenation().get(currentDot - 1);
    }

    /**
     * 函数功能：获取点前进后的产生式项目。
     * 输入：
     * - 无。
     * 输出：ProductionItem 类型返回值。
     */

    @Override
    public ProductionItem nextItem() {
        if (!hasNextSymbol()) {
            throw new IllegalStateException("next item is not exist!");
        }
        return new ProductionItemImpl(context, productionIndex, alternationIndex, currentDot + 1);
    }

    /**
     * 函数功能：获取点后语法符号的可迭代对象。
     * 输入：
     * - 无。
     * 输出：Iterable<GrammarUnitSymbol> 类型集合或迭代结果。
     */

    @Override
    public Iterable<GrammarUnitSymbol> afterIterable() {
        AlterableSymbol alterableSymbol = getAlterableSymbol();
        if (alterableSymbol.isEpsilon()) {
            throw new UnsupportedOperationException("Epsilon body do not have after iterable.");
        }
        return new AfterIterable<>(currentDot, alterableSymbol.toConcatenation());
    }

    /**
     * 函数功能：判断当前产生式项目是否为空串。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean isEpsilon() {
        return getAlterableSymbol().isEpsilon();
    }

    /**
     * 函数功能：判断当前对象是否与指定对象相等。
     * 输入：
     * - o：Object 类型参数。
     * 输出：判断结果布尔值。
     */

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

    /**
     * 函数功能：返回当前对象的哈希值。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    @Override
    public int hashCode() {
        return Objects.hash(currentDot, productionIndex, alternationIndex);
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

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
