package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 00:23
 */
public interface TerminalMatcher {
    int indexOf(SourceToken token);
}
