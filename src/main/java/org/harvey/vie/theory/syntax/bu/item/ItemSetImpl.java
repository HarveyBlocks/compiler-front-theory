package org.harvey.vie.theory.syntax.bu.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 17:32
 */
@AllArgsConstructor
class ItemSetImpl implements ItemSet {
    private final Set<ProductionItem> set;
    @Getter
    private final Map<TerminalSymbol, Integer> terminalGoto;
    @Getter
    private final Map<HeadSymbol, Integer> headGoto;
    private final Map<HeadSymbol, Set<TerminalSymbol>> decisionRules;

    /**
     * 函数功能：判断是否包含指定元素。
     * 输入：
     * - item：ProductionItem 类型参数。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean contains(ProductionItem item) {
        return set.contains(item);
    }

    /**
     * 函数功能：获取元素数量。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    @Override
    public int size() {
        return set.size();
    }

    /**
     * 函数功能：获取指定语法符号的转移目标。
     * 输入：
     * - unit：GrammarUnitSymbol 类型参数。
     * 输出：整数结果。
     */

    @Override
    public int gotoUnit(GrammarUnitSymbol unit) {
        Integer set = unit.isTerminal() ? terminalGoto.get(unit.toTerminal()) : headGoto.get(unit.toHead());
        return set == null ? NONE : set;
    }

    /**
     * 函数功能：获取指定非终结符的决策规则。
     * 输入：
     * - head：HeadSymbol 类型参数。
     * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
     */

    @Override
    public Set<TerminalSymbol> decisionRule(HeadSymbol head) {
        Set<TerminalSymbol> set = decisionRules.get(head);
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * 函数功能：获取决策规则映射。
     * 输入：
     * - 无。
     * 输出：Set<TerminalSymbol>> 类型集合或迭代结果。
     */


    @Override
    public Map<HeadSymbol, Set<TerminalSymbol>> getDecisionRule() {
        return decisionRules;
    }

    /**
     * 函数功能：获取当前对象的迭代器。
     * 输入：
     * - 无。
     * 输出：Iterator<ProductionItem> 类型集合或迭代结果。
     */


    @Override
    public Iterator<ProductionItem> iterator() {
        return set.iterator();
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return set.stream().map(Objects::toString).map(s -> '`' + s + '`').collect(Collectors.joining(", ", "{", "}"));
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
        if (!(o instanceof ItemSetImpl)) {
            return false;
        }
        ItemSetImpl that = (ItemSetImpl) o;
        return Objects.equals(set, that.set);
    }

    /**
     * 函数功能：返回当前对象的哈希值。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }
}
