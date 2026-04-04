package org.harvey.vie.theory.syntax.grammar.follow;

import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:42
 */
public class FollowSetImpl implements FollowSet {
    ;
    private final Set<TerminalSymbol> set;
    private final boolean containsEndMarker;

    public FollowSetImpl(Set<TerminalSymbol> set, boolean containsEndMarker) {
        this.set = set;
        this.containsEndMarker = containsEndMarker;
    }

    @Override
    public Set<TerminalSymbol> followExceptEndMarker() {
        return Collections.unmodifiableSet(set);
    }

    @Override
    public boolean containsEndMarker() {
        return containsEndMarker;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        for (TerminalSymbol terminalSymbol : set) {
            sj.add("'" + terminalSymbol.toString() + "'");
        }
        if (containsEndMarker) {
            sj.add("'$'");
        }
        return sj.toString();
    }
}
