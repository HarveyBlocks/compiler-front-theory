package org.harvey.vie.theory.syntax.td.follow;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.td.first.FirstMap;
import org.harvey.vie.theory.syntax.td.first.FirstSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:43
 */
public class FollowSetFactoryImpl implements FollowSetFactory {
    @Override
    public FollowMap follow(
            String startHead, ProductionSetContext context, FirstMap firstMap) {
        FollowMapBuilder mapBuilder = new FollowMapBuilder(context);
        HeadDefineSymbol start = mapBuilder.getDefinition(startHead);
        // 规则一, 标注开始
        follow1(start, mapBuilder);
        // 规则二
        follow2(start, firstMap, mapBuilder);
        // 规则三
        follow3(start, firstMap, mapBuilder);
        return mapBuilder.buildMap();
    }

    private static void follow1(HeadSymbol start, FollowMapBuilder mapBuilder) {
        mapBuilder.getBuilder(start).containsEndMarker = true;
    }

    private void follow2(HeadDefineSymbol start, FirstMap firstMap, FollowMapBuilder mapBuilder) {
        forEach(start, mapBuilder, (headBuilder, headSymbol, afterIterator) -> {
            FollowSetBuilder headFollowSetBuilder = mapBuilder.getBuilder(headSymbol);
            cupAssignFirstAfter(headFollowSetBuilder, firstMap, afterIterator);
            return true;
        });
    }

    private void follow3(HeadDefineSymbol start, FirstMap firstMap, FollowMapBuilder mapBuilder) {
        boolean changed;
        do {
            changed = forEach(start, mapBuilder, (headBuilder, headSymbol, afterIterator) -> {
                FollowSetBuilder headFollowSetBuilder = mapBuilder.getBuilder(headSymbol);
                if (afterFirstContainsEpsilon(firstMap, afterIterator)) {
                    return cupAssignHeadFollow(headFollowSetBuilder, headBuilder);
                }
                return false;
            });
        } while (changed);
    }

    private boolean forEach(
            HeadSymbol start, FollowMapBuilder mapBuilder, Func function) {
        Set<HeadSymbol> visited = new HashSet<>();
        HeadQueue queue = new HeadQueue();
        queue.addLast(start);
        boolean changed = false;
        while (queue.hasElement()) {
            HeadSymbol headSymbol = queue.removeFirst();
            if (visited.contains(headSymbol)) {
                continue;
            }
            changed = forEachProduction(headSymbol, mapBuilder, queue, function);
            visited.add(headSymbol);
        }
        return changed;
    }

    private boolean forEachProduction(
            HeadSymbol head, FollowMapBuilder mapBuilder, HeadQueue queue, Func function) {
        GrammarAlternation alternation = mapBuilder.getAlternation(head);
        FollowSetBuilder headBuilder = mapBuilder.getBuilder(head);
        boolean changed = false;
        for (GrammarSymbol symbol : alternation) {
            if (!symbol.isEpsilon()) {
                if (symbol.isConcatenation()) {
                    GrammarConcatenation concatenation = symbol.toConcatenation();
                    changed = forEachProduction(headBuilder, concatenation, queue, function);
                } else {
                    throw new IllegalStateException("Unknown type of: " + symbol.getClass());
                }
            }
            // epsilon
            // 不处理
        }
        return changed;
    }

    private boolean forEachProduction(
            FollowSetBuilder headBuilder, GrammarConcatenation concatenation, HeadQueue queue, Func function) {
        boolean changed = false;
        for (int i = 0; i < concatenation.size(); i++) {
            ConcatenableSymbol symbol = concatenation.get(i);
            requireNotConcatenation(symbol);
            if (symbol.isTerminal()) {
                // 终结符? 不关心
                continue;
            }
            HeadSymbol headSymbol = symbol.toHead();
            queue.addLast(headSymbol);
            changed = function.invoke(headBuilder, headSymbol, new AfterIterable(i, concatenation));
        }
        return changed;
    }

    private void cupAssignFirstAfter(FollowSetBuilder builder, FirstMap firstMap, AfterIterable afterIterable) {
        builder.set.addAll(firstMap.first(afterIterable).firstExceptEpsilon());
    }

    private boolean afterFirstContainsEpsilon(FirstMap firstMap, AfterIterable afterIterable) {
        for (ConcatenableSymbol symbol : afterIterable) {
            requireNotConcatenation(symbol);
            if (symbol.isTerminal()) {
                return false;
            } else {
                FirstSet firstSet = firstMap.get(symbol.toHead());
                if (!firstSet.containsEpsilon()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean cupAssignHeadFollow(
            FollowSetBuilder builder, FollowSetBuilder headBuilder) {
        int oldSize = builder.set.size();
        boolean oldContainsEndMarker = builder.containsEndMarker;
        builder.set.addAll(headBuilder.set);
        builder.containsEndMarker = headBuilder.containsEndMarker || builder.containsEndMarker;
        return !(builder.set.size() == oldSize /*size 不同, 就是改变了*/ &&
                 builder.containsEndMarker == oldContainsEndMarker);
    }


    private void requireNotConcatenation(ConcatenableSymbol symbol) {
        if (symbol.isConcatenation()) {
            throw new IllegalStateException("require non-concatenation grammar symbol here.");
        }
    }

    @FunctionalInterface
    private interface Func {
        boolean invoke(FollowSetBuilder headBuilder, HeadSymbol headSymbol, AfterIterable afterIterable);
    }

    private static class AfterIterable implements Iterable<ConcatenableSymbol> {
        private final int offset;
        private final GrammarConcatenation concatenation;

        public AfterIterable(int offset, GrammarConcatenation concatenation) {
            this.offset = offset;
            this.concatenation = concatenation;
        }

        @Override
        public java.util.Iterator<ConcatenableSymbol> iterator() {
            return new Iterator();
        }

        private class Iterator implements java.util.Iterator<ConcatenableSymbol> {
            private int pos = offset + 1;

            @Override
            public boolean hasNext() {
                return pos < concatenation.size();
            }

            @Override
            public ConcatenableSymbol next() {
                return concatenation.get(pos++);
            }
        }
    }


    private static class HeadQueue {
        private final LinkedList<HeadSymbol> queue = new LinkedList<>();

        public HeadSymbol removeFirst() {
            return queue.removeFirst();
        }

        public void addLast(HeadSymbol headSymbol) {
            queue.addLast(headSymbol);
        }

        public boolean hasElement() {
            return !queue.isEmpty();
        }
    }

    private static class FollowSetBuilder {
        private boolean containsEndMarker = false;
        private final Set<TerminalSymbol> set = new HashSet<>();

        public FollowSet build() {
            return new FollowSetImpl(set, containsEndMarker);
        }
    }

    private static class FollowMapBuilder {
        private final ProductionSetContext context;
        private final Map<HeadSymbol, FollowSetBuilder> followMap;


        public FollowMapBuilder(ProductionSetContext context) {
            this.context = context;
            followMap = new HashMap<>();
        }

        public HeadDefineSymbol getDefinition(String startHead) {
            return context.getDefinition(startHead);
        }

        public GrammarAlternation getAlternation(HeadSymbol head) {
            return context.getAlternation(head);
        }

        public FollowSetBuilder getBuilder(HeadSymbol head) {
            return followMap.computeIfAbsent(head, k -> new FollowSetBuilder());
        }

        public FollowMap buildMap() {
            Map<HeadSymbol, FollowSet> collect = followMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
            return new FollowMapImpl(collect);
        }

    }
}
