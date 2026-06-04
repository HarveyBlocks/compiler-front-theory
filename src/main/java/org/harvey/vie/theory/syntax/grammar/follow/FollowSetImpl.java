package org.harvey.vie.theory.syntax.grammar.follow;

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
public class FollowSetImpl implements FollowSet {

    private final Set<TerminalSymbol> set;
    private final boolean containsEndMarker;
/**
 * 函数功能：创建 FollowSetImpl 对象。
 * 输入：
 * - set：Set<TerminalSymbol> 类型参数。
 * - containsEndMarker：boolean 类型参数。
 * 输出：无。
 */

    public FollowSetImpl(Set<TerminalSymbol> set, boolean containsEndMarker) {
        this.set = set;
        this.containsEndMarker = containsEndMarker;
    }
/**
 * 函数功能：获取不含结束标记的 FOLLOW 集合。
 * 输入：
 * - 无。
 * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
 */

    @Override
    public Set<TerminalSymbol> followExceptEndMarker() {
        return Collections.unmodifiableSet(set);
    }
/**
 * 函数功能：判断是否包含指定内容。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean containsEndMarker() {
        return containsEndMarker;
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
        if (containsEndMarker) {
            sj.add("'$'");
        }
        return sj.toString();
    }
}
