package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 00:23
 */
public interface TerminalMatcher {
    /**
     * 函数功能：获取指定对象的索引。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：整数结果。
     */
    int indexOf(SourceToken token);
}
