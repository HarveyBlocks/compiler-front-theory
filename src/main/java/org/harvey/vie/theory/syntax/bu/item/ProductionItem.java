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
     * 函数功能：获取当前点的位置。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */
    int currentDot();

    /**
     * 函数功能：获取产生式头部符号。
     * 输入：
     * - 无。
     * 输出：HeadSymbol 类型返回值。
     */

    HeadSymbol getHead();

    /**
     * 函数功能：获取可候选语法符号。
     * 输入：
     * - 无。
     * 输出：AlterableSymbol 类型返回值。
     */

    AlterableSymbol getAlterableSymbol();

    /**
     * 函数功能：判断两个项目是否属于同一产生式。
     * 输入：
     * - o：ProductionItem 类型参数。
     * 输出：判断结果布尔值。
     */

    boolean productionEquals(ProductionItem o);

    /**
     * 函数功能：判断点后是否存在语法符号。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean hasNextSymbol();

    /**
     * 函数功能：判断点前是否存在语法符号。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean hasPreviousSymbol();

    /**
     * 函数功能：获取点后的语法符号。
     * 输入：
     * - 无。
     * 输出：GrammarUnitSymbol 类型返回值。
     */

    GrammarUnitSymbol nextSymbol();

    /**
     * 函数功能：获取点前的语法符号。
     * 输入：
     * - 无。
     * 输出：GrammarUnitSymbol 类型返回值。
     */

    GrammarUnitSymbol previousSymbol();

    /**
     * 函数功能：获取点前进后的产生式项目。
     * 输入：
     * - 无。
     * 输出：ProductionItem 类型返回值。
     */

    ProductionItem nextItem();

    /**
     * 函数功能：获取点后语法符号的可迭代对象。
     * 输入：
     * - 无。
     * 输出：Iterable<GrammarUnitSymbol> 类型集合或迭代结果。
     */

    Iterable<GrammarUnitSymbol> afterIterable();

    /**
     * 函数功能：判断当前产生式项目是否为空串。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean isEpsilon();


}
