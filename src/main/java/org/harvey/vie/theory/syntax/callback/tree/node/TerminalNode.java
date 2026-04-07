package org.harvey.vie.theory.syntax.callback.tree.node;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO 叶子
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 18:41
 */
public interface TerminalNode extends GrammarSyntaxTreeNode {
    TerminalSymbol getSymbol();

    SourceToken getToken();
}
