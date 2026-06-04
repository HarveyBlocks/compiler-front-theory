package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:42
 */
public class FirstSetImpl implements FirstSet {
    private final Set<TerminalSymbol> set;
    private final boolean containsEpsilon;

    /**
     * 函数功能：创建 FirstSetImpl 对象。
     * 输入：
     * - set：Set<TerminalSymbol> 类型参数。
     * - containsEpsilon：boolean 类型参数。
     * 输出：无。
     */

    public FirstSetImpl(Set<TerminalSymbol> set, boolean containsEpsilon) {
        this.set = set;
        this.containsEpsilon = containsEpsilon;
    }

    /**
     * 函数功能：获取不包含空串的 FIRST 集合。
     * 输入：
     * - 无。
     * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
     */

    @Override
    public Set<TerminalSymbol> firstExceptEpsilon() {
        return Collections.unmodifiableSet(set);
    }

    /**
     * 函数功能：判断是否包含指定元素。
     * 输入：
     * - symbol：GrammarSymbol 类型参数。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean contains(GrammarSymbol symbol) {
        if (symbol == null) {
            return false;
        }
        if (symbol.isEpsilon()) {
            return containsEpsilon;
        }
        if (!symbol.isConcatenable() || !symbol.isTerminal()) {
            return false;
        }
        return set.contains(symbol.toTerminal());
    }

    /**
     * 函数功能：判断集合是否包含空串符号。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean containsEpsilon() {
        return containsEpsilon;
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        for (TerminalSymbol terminalSymbol : set) {
            sj.add("'" + terminalSymbol.toString() + "'");
        }
        if (containsEpsilon) {
            sj.add("'" + GrammarSymbol.epsilon() + "'");
        }
        return sj.toString();
    }
}
