package org.harvey.vie.theory.lexical.regex.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A {@link RegexNode} representing the concatenation of two regular expression
 * patterns. It requires both patterns to match sequentially.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:36
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConcatenationRegexNode implements RegexNode {
    private RegexNode left;
    private RegexNode right;

    /**
     * 函数功能：返回连接正则节点的字符串表示。
     * 输入：
     * - 无。
     * 输出：连接正则节点的字符串表示。
     */
    @Override
    public String toString() {
        return "(" + left + ")(" + right + ")";
    }
}
