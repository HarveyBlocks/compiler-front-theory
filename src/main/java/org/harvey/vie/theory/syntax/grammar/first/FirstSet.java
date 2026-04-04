package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Collections;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:40
 */
public interface FirstSet {
    FirstSet EPSILON = new EpsilonFirstSet();

    Set<TerminalSymbol> firstExceptEpsilon();
    boolean contains(GrammarSymbol symbol);

    boolean containsEpsilon();
}

class EpsilonFirstSet implements FirstSet {

    @Override
    public Set<TerminalSymbol> firstExceptEpsilon() {
        return Collections.emptySet();
    }

    @Override
    public boolean contains(GrammarSymbol symbol) {
        return false;
    }

    @Override
    public boolean containsEpsilon() {
        return true;
    }
}