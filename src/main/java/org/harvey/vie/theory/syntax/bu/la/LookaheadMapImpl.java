package org.harvey.vie.theory.syntax.bu.la;

import org.harvey.vie.theory.syntax.bu.item.ProductionItem;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 18:24
 */
public class LookaheadMapImpl implements LookaheadMap {

    private final Map<ProductionItem, Set<TerminalSymbol>> map;

    /**
     * 函数功能：创建 LookaheadMapImpl 对象。
     * 输入：
     * - map：Map<ProductionItem, Set<TerminalSymbol>> 类型参数。
     * 输出：无。
     */

    public LookaheadMapImpl(Map<ProductionItem, Set<TerminalSymbol>> map) {
        this.map = map;
    }

    /**
     * 函数功能：判断是否包含指定元素。
     * 输入：
     * - item：ProductionItem 类型参数。
     * - terminalSymbol：TerminalSymbol 类型参数。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean contains(ProductionItem item, TerminalSymbol terminalSymbol) {
        Set<TerminalSymbol> lookahead = map.get(item);
        if (lookahead == null) {
            return false;
        }
        return lookahead.contains(terminalSymbol);
    }

    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - item：ProductionItem 类型参数。
     * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
     */

    @Override
    public Set<TerminalSymbol> get(ProductionItem item) {
        return Optional.ofNullable(map.get(item)).orElseGet(Collections::emptySet);
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return map.entrySet()
                .stream()
                .map(e -> "`" +
                          e.getKey() +
                          "`: " +
                          e.getValue().stream().map(Objects::toString).collect(Collectors.joining(",", "{", "}")))
                .collect(Collectors.joining(",", "{", "}"));
    }
}
