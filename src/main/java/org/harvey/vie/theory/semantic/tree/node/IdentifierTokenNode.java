package org.harvey.vie.theory.semantic.tree.node;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 01:03
 */
@Getter
public class IdentifierTokenNode extends TokenNode implements ShiftReduceSyntaxTreeNode {
    private final int no;
    private final int offset;

    /**
     * 函数功能：创建 IdentifierTokenNode 对象。
     * 输入：
     * - source：SourceToken 类型参数。
     * - no：int 类型参数。
     * - offset：int 类型参数。
     * 输出：无。
     */

    public IdentifierTokenNode(SourceToken source, int no, int offset) {
        super(source);
        this.no = no;
        this.offset = offset;
    }

    /**
     * 函数功能：判断节点是否为标识符节点。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean isIdentifier() {
        return true;
    }
}
