package org.harvey.vie.theory.semantic.tree.node;

import lombok.Getter;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 01:02
 */
@Getter
public class BlockNode extends HeadNode {
    private final IdentifierRecord[] scope;
/**
 * 函数功能：创建 BlockNode 对象。
 * 输入：
 * - symbol：HeadSymbol 类型参数。
 * - production：SimpleGrammarProduction 类型参数。
 * - children：ShiftReduceSyntaxTreeNode[] 类型参数。
 * - scope：IdentifierRecord[] 类型参数。
 * 输出：无。
 */

    public BlockNode(
            HeadSymbol symbol,
            SimpleGrammarProduction production,
            ShiftReduceSyntaxTreeNode[] children,
            IdentifierRecord[] scope) {
        super(symbol, production, children);
        this.scope = scope;
    }

}
