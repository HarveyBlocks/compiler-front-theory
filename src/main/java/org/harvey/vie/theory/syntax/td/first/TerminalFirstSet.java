package org.harvey.vie.theory.syntax.td.first;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:07
 */
@AllArgsConstructor
public class TerminalFirstSet implements FirstSet {
    private final TerminalSymbol symbol;

    @Override
    public Set<TerminalSymbol> firstExceptEpsilon() {
        return Set.of(symbol);
    }

    @Override
    public boolean contains(GrammarSymbol symbol) {
        return this.symbol.equals(symbol);
    }

    @Override
    public boolean containsEpsilon() {
        return false;
    }

    @Override
    public String toString() {
        return "{'" + symbol + "'}";
    }
}
