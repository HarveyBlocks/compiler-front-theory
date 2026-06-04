package org.harvey.vie.theory.lexical.regex.node;

/**
 * A {@link RegexNode} representing an empty string (epsilon). It matches
 * without consuming any input.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:24
 */
public class EpsilonRegexNode implements RegexNode {
    /**
     * 函数功能：返回空串正则节点的字符串表示。
     * 输入：
     * - 无。
     * 输出：空串正则节点的字符串表示。
     */
    @Override
    public String toString() {
        return "ε";
    }
}
