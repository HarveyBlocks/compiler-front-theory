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
    /**
     * 函数功能：判断是否包含指定元素。
     * 输入：
     * - item：ProductionItem 类型参数。
     * - terminalSymbol：TerminalSymbol 类型参数。
     * 输出：判断结果布尔值。
     */

    boolean contains(ProductionItem item, TerminalSymbol terminalSymbol);

    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - item：ProductionItem 类型参数。
     * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
     */

    Set<TerminalSymbol> get(ProductionItem item);
}
