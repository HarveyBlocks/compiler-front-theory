package org.harvey.vie.theory.syntax.td.conflict;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO 把一个token进行分割
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 22:34
 */
public interface ConflictTokenSplitter {
    SourceTokenIterator split(TerminalSymbol terminalSymbol, SourceToken token);
}
