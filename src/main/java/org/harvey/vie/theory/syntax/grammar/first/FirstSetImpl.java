package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarSymbol;
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
public class FirstSetImpl implements FirstSet {
    private final Set<TerminalSymbol> set;
    private final boolean containsEpsilon;

    public FirstSetImpl(Set<TerminalSymbol> set, boolean containsEpsilon) {
        this.set = set;
        this.containsEpsilon = containsEpsilon;
    }

    @Override
    public Set<TerminalSymbol> firstExceptEpsilon() {
        return Collections.unmodifiableSet(set);
    }

    @Override
    public boolean contains(GrammarSymbol symbol) {
        if (symbol == null) {
            return false;
        }
        if (symbol.isEpsilon()) {
            return containsEpsilon;
        }
        if (!symbol.isConcatenable() || !symbol.isTerminal()) {
            return false;
        }
        return set.contains(symbol.toTerminal());
    }

    @Override
    public boolean containsEpsilon() {
        return containsEpsilon;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        for (TerminalSymbol terminalSymbol : set) {
            sj.add("'" + terminalSymbol.toString() + "'");
        }
        if (containsEpsilon) {
            sj.add("'" + GrammarSymbol.EPSILON + "'");
        }
        return sj.toString();
    }
}
