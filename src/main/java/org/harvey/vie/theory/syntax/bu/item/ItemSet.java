package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.util.SimpleCollection;

import java.util.Map;
import java.util.Set;

/**
 * TODO 项集
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 22:13
 */
public interface ItemSet extends SimpleCollection<ProductionItem> {
    int NONE = -1;

    /**
     * 函数功能：判断是否包含指定元素。
     * 输入：
     * - item：ProductionItem 类型参数。
     * 输出：判断结果布尔值。
     */

    boolean contains(ProductionItem item);

    /**
     * 函数功能：获取指定语法符号的转移目标。
     * 输入：
     * - unit：GrammarUnitSymbol 类型参数。
     * 输出：整数结果。
     */


    int gotoUnit(GrammarUnitSymbol unit);

    /**
     * 函数功能：获取指定非终结符的决策规则。
     * 输入：
     * - head：HeadSymbol 类型参数。
     * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
     */

    Set<TerminalSymbol> decisionRule(HeadSymbol head);


    Map<HeadSymbol, Set<TerminalSymbol>> getDecisionRule();

    Map<HeadSymbol, Integer> getHeadGoto();

    Map<TerminalSymbol, Integer> getTerminalGoto();
}
