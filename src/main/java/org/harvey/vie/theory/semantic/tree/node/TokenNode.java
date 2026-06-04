package org.harvey.vie.theory.semantic.tree.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 01:02
 */
@AllArgsConstructor
@Getter
public class TokenNode implements ShiftReduceSyntaxTreeNode {
    private final SourceToken source;

    /**
     * 函数功能：判断节点是否为词法单元节点。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean isToken() {
        return true;
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */


    @Override
    public String toString() {
        return source.hintString();
    }

    /**
     * 函数功能：创建标识符词法节点。
     * 输入：
     * - no：int 类型参数。
     * - offset：int 类型参数。
     * 输出：ShiftReduceSyntaxTreeNode 类型返回值。
     */

    public ShiftReduceSyntaxTreeNode instanceIdentifier(int no, int offset) {
        return new IdentifierTokenNode(source, no, offset);
    }

    /**
     * 函数功能：判断节点是否为标识符节点。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    public boolean isIdentifier() {
        return false;
    }

    /**
     * 函数功能：转换为标识符节点。
     * 输入：
     * - 无。
     * 输出：IdentifierTokenNode 类型返回值。
     */

    public IdentifierTokenNode toIdentifier() {
        return (IdentifierTokenNode) this;
    }

}
