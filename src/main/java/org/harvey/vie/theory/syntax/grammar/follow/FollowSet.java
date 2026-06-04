package org.harvey.vie.theory.syntax.grammar.follow;

import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:40
 */
public interface FollowSet {
    /**
     * 函数功能：获取不含结束标记的 FOLLOW 集合。
     * 输入：
     * - 无。
     * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
     */
    Set<TerminalSymbol> followExceptEndMarker();

    /**
     * 函数功能：判断是否包含指定内容。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean containsEndMarker();
}
