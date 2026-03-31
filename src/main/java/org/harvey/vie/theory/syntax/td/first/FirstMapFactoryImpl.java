package org.harvey.vie.theory.syntax.td.first;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:43
 */
public class FirstMapFactoryImpl implements FirstMapFactory {
    @Override
    public FirstMap first(ProductionSetContext context) {
        FirstMapBuilder builder = new FirstMapBuilder(context);
        for (HeadSymbol head : context.headIterable()) {
            first(head, builder);
        }
        return builder.build();
    }

    private FirstSetBuilder first(HeadSymbol head, FirstMapBuilder mapBuilder) {
        FirstSetBuilder builder = mapBuilder.getBuilder(head);
        for (GrammarSymbol symbol : mapBuilder.getAlternation(head)) {
            if (symbol.isEpsilon()) {
                builder.containsEpsilon = true;
            } else if (symbol.isConcatenation()) {
                GrammarConcatenation concatenation = symbol.toConcatenation();
                first(concatenation, builder, mapBuilder);
            } else {
                throw new IllegalStateException("Unknown type of: " + symbol.getClass());
            }
        }
        return builder;
    }

    private void first(GrammarConcatenation concatenation, FirstSetBuilder setBuilder, FirstMapBuilder mapBuilder) {
        if (concatenation.isEmpty()) {
            throw new IllegalStateException("GrammarConcatenation can not be empty!");
        }
        boolean allEpsilon = true;
        for (ConcatenableSymbol concatenable : concatenation) {
            FirstSetBuilder innerBuilder = innerBuilder(concatenable, mapBuilder);
            setBuilder.set.addAll(innerBuilder.set);
            if (!innerBuilder.containsEpsilon) {
                allEpsilon = false;
                break;
            }
            // 如果有
            // continue
        }
        setBuilder.containsEpsilon = allEpsilon;
        mapBuilder.addAllTerminal(concatenation);
    }

    private FirstSetBuilder innerBuilder(ConcatenableSymbol concatenable, FirstMapBuilder builder) {
        if (concatenable.isTerminal()) {
            return FirstSetBuilder.terminal(concatenable.toTerminal());
        } else {
            return first(concatenable.toHead(), builder);
        }
    }

    static class FirstSetBuilder {
        private boolean containsEpsilon = false;
        private final Set<TerminalSymbol> set = new HashSet<>();

        public static FirstSetBuilder terminal(TerminalSymbol terminal) {
            FirstSetBuilder builder = new FirstSetBuilder();
            builder.set.add(terminal);
            builder.containsEpsilon = false;
            return builder;
        }

        private FirstSet build() {
            return new FirstSetImpl(set, containsEpsilon);
        }
    }

    static class FirstMapBuilder {
        private final ProductionSetContext context;
        private final Map<HeadSymbol, FirstSetBuilder> firstMap;
        private final Set<TerminalSymbol> terminalSet;

        FirstMapBuilder(ProductionSetContext context) {
            this.context = context;
            firstMap = new HashMap<>();
            terminalSet = new HashSet<>();
        }

        private FirstSetBuilder getBuilder(HeadSymbol head) {
            return firstMap.computeIfAbsent(head, k -> new FirstSetBuilder());
        }

        public GrammarAlternation getAlternation(HeadSymbol head) {
            return context.getAlternation(head);
        }
        public FirstMap build() {
            Map<HeadSymbol, FirstSet> built = firstMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
            return new FirstMapImpl(built, terminalSet);
        }

        private void addAllTerminal(GrammarConcatenation concatenation) {
            concatenation.stream()
                    .filter(ConcatenableSymbol::isTerminal)
                    .map(ConcatenableSymbol::toTerminal)
                    .forEach(terminalSet::add);
        }

    }
}
