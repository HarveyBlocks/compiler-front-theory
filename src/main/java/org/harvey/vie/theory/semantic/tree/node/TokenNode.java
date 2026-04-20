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

    @Override
    public boolean isToken() {
        return true;
    }

    @Override
    public String toString() {
        return source.hintString();
    }

    public ShiftReduceSyntaxTreeNode instanceIdentifier(int no) {
        return new IdentifierTokenNode(source, no);
    }

    public boolean isIdentifier() {
        return false;
    }

    public IdentifierTokenNode toIdentifier() {
        return (IdentifierTokenNode) this;
    }

}
