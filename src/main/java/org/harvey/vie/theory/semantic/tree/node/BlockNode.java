package org.harvey.vie.theory.semantic.tree.node;

import lombok.Getter;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
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

    public BlockNode(HeadSymbol symbol, ShiftReduceSyntaxTreeNode[] children, IdentifierRecord[] scope) {
        super(symbol, children);
        this.scope = scope;
    }

}
