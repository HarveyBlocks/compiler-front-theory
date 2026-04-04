package org.harvey.vie.theory.syntax.grammar.first;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 19:40
 */
public class FirstSetBuilder {
    private final Set<TerminalSymbol> set = new HashSet<>();
    @Getter
    @Setter
    private boolean containsEpsilon = false;

    public static FirstSetBuilder terminal(TerminalSymbol terminal) {
        FirstSetBuilder builder = new FirstSetBuilder();
        builder.set.add(terminal);
        builder.containsEpsilon = false;
        return builder;
    }

    public void addAllExceptEpsilon(FirstSetBuilder builder) {
        set.addAll(builder.set);
    }

    public FirstSet build() {
        return new FirstSetImpl(set, containsEpsilon);
    }

    public int setSize() {
        return set.size();
    }
}
