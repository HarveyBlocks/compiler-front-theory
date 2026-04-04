package org.harvey.vie.theory.syntax.td.tree.node;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-02 04:23
 */
@AllArgsConstructor
public class TerminalNodeImpl implements TerminalNode {
    private final TerminalSymbol symbol;
    private final SourceToken token;

    @Override
    public TerminalSymbol getSymbol() {
        return symbol;
    }

    @Override
    public SourceToken getToken() {
        return token;
    }

    @Override
    public String toString() {
        return token.hintString();
    }
}
