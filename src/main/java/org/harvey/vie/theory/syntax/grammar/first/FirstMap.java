package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.util.SimpleCollection;

import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:03
 */
public interface FirstMap extends SimpleCollection<Map.Entry<GrammarUnitSymbol, FirstSet>> {
    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - head：HeadSymbol 类型参数。
     * 输出：FirstSet 类型返回值。
     */
    FirstSet get(HeadSymbol head);
/**
 * 函数功能：获取指定位置或键对应的元素。
 * 输入：
 * - terminal：TerminalSymbol 类型参数。
 * 输出：FirstSet 类型返回值。
 */

    FirstSet get(TerminalSymbol terminal);
/**
 * 函数功能：获取终结符集合。
 * 输入：
 * - 无。
 * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
 */

    Set<TerminalSymbol> terminalSet();
    /**
     * 函数功能：获取非终结符集合。
     * 输入：
     * - 无。
     * 输出：Set<HeadSymbol> 类型集合或迭代结果。
     */
    Set<HeadSymbol> headSet();
/**
 * 函数功能：获取 FIRST 集合。
 * 输入：
 * - iterable：Iterable<GrammarUnitSymbol> 类型参数。
 * 输出：FirstSet 类型返回值。
 */

    FirstSet first(Iterable<GrammarUnitSymbol> iterable);
/**
 * 函数功能：判断指定符号是否可推出空串。
 * 输入：
 * - iterable：Iterable<GrammarUnitSymbol> 类型参数。
 * 输出：判断结果布尔值。
 */

    boolean nullable(Iterable<GrammarUnitSymbol> iterable);
}
