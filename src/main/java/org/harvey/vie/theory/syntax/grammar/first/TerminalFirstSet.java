package org.harvey.vie.theory.syntax.grammar.first;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:07
 */
@AllArgsConstructor
public class TerminalFirstSet implements FirstSet {
    private final TerminalSymbol symbol;

    /**
     * 函数功能：获取不包含空串的 FIRST 集合。
     * 输入：
     * - 无。
     * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
     */

    @Override
    public Set<TerminalSymbol> firstExceptEpsilon() {
        return Set.of(symbol);
    }

    /**
     * 函数功能：判断是否包含指定元素。
     * 输入：
     * - symbol：GrammarSymbol 类型参数。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean contains(GrammarSymbol symbol) {
        return this.symbol.equals(symbol);
    }

    /**
     * 函数功能：判断集合是否包含空串符号。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean containsEpsilon() {
        return false;
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return "{'" + symbol + "'}";
    }
}
