package org.harvey.vie.theory.syntax.td.follow;

import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:40
 */
public interface FollowSet {
    Set<TerminalSymbol> followExceptEndMarker();
    boolean containsEndMarker();
}
