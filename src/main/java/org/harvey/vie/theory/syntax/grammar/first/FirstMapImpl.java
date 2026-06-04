package org.harvey.vie.theory.syntax.grammar.first;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:04
 */
@AllArgsConstructor
public class FirstMapImpl implements FirstMap {
    private final Map<HeadSymbol, FirstSet> headMap;
    private final Set<TerminalSymbol> terminalSet;
/**
 * 函数功能：获取指定位置或键对应的元素。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * 输出：FirstSet 类型返回值。
 */

    @Override
    public FirstSet get(HeadSymbol head) {
        return headMap.get(head);
    }
/**
 * 函数功能：获取指定位置或键对应的元素。
 * 输入：
 * - terminal：TerminalSymbol 类型参数。
 * 输出：FirstSet 类型返回值。
 */

    @Override
    public FirstSet get(TerminalSymbol terminal) {
        return terminalSet.contains(terminal) ? new TerminalFirstSet(terminal) : null;
    }
/**
 * 函数功能：获取终结符集合。
 * 输入：
 * - 无。
 * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
 */

    @Override
    public Set<TerminalSymbol> terminalSet() {
        return terminalSet;
    }
/**
 * 函数功能：获取非终结符集合。
 * 输入：
 * - 无。
 * 输出：Set<HeadSymbol> 类型集合或迭代结果。
 */

    @Override
    public Set<HeadSymbol> headSet() {
        return headMap.keySet();
    }
/**
 * 函数功能：获取 FIRST 集合。
 * 输入：
 * - iterable：Iterable<GrammarUnitSymbol> 类型参数。
 * 输出：FirstSet 类型返回值。
 */

    @Override
    public FirstSet first(Iterable<GrammarUnitSymbol> iterable) {
        Set<TerminalSymbol> allFirstSet = new HashSet<>();
        boolean containsEpsilon = true;
        for (GrammarUnitSymbol symbol : iterable) {
            FirstSet firstSet = symbol.isTerminal() ? get(symbol.toTerminal()) : get(symbol.toHead());
            allFirstSet.addAll(firstSet.firstExceptEpsilon());
            if (!firstSet.containsEpsilon()) {
                containsEpsilon = false;
                break;
            }  // 可空则继续
        }
        return new FirstSetImpl(allFirstSet, containsEpsilon);
    }
/**
 * 函数功能：判断指定符号是否可推出空串。
 * 输入：
 * - iterable：Iterable<GrammarUnitSymbol> 类型参数。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean nullable(Iterable<GrammarUnitSymbol> iterable) {
        boolean nullable = true;
        for (GrammarUnitSymbol symbol : iterable) {
            FirstSet firstSet = symbol.isTerminal() ? get(symbol.toTerminal()) : get(symbol.toHead());
            if (!firstSet.containsEpsilon()) {
                nullable = false;
                break;
            }  // 可空则继续
        }
        return nullable;
    }
/**
 * 函数功能：获取当前对象的迭代器。
 * 输入：
 * - 无。
 * 输出：FirstSet>> 类型返回值。
 */

    @Override
    public Iterator<Map.Entry<GrammarUnitSymbol, FirstSet>> iterator() {
        return new EntryIterator();
    }
/**
 * 函数功能：获取元素数量。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    @Override
    public int size() {
        return headMap.size() + terminalSet.size();
    }

    private class EntryIterator implements Iterator<Map.Entry<GrammarUnitSymbol, FirstSet>> {
        private final Iterator<Map.Entry<HeadSymbol, FirstSet>> headIter = headMap.entrySet().iterator();
        private final Iterator<TerminalSymbol> terminalIter = terminalSet.iterator();
/**
 * 函数功能：判断是否存在下一个元素。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

        @Override
        public boolean hasNext() {
            return headIter.hasNext() || terminalIter.hasNext();
        }
/**
 * 函数功能：获取下一个元素。
 * 输入：
 * - 无。
 * 输出：FirstSet> 类型返回值。
 */

        @Override
        public Map.Entry<GrammarUnitSymbol, FirstSet> next() {
            if (headIter.hasNext()) {
                Map.Entry<HeadSymbol, FirstSet> next = headIter.next();
                return Map.entry(next.getKey(), next.getValue());
            }
            TerminalSymbol next = terminalIter.next();
            return Map.entry(next, get(next));
        }
    }
}
