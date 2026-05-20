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

    public IdentifierTokenNode(SourceToken source, int no, int offset) {
        super(source);
        this.no = no;
        this.offset = offset;
    }

    @Override
    public boolean isIdentifier() {
        return true;
    }
}
