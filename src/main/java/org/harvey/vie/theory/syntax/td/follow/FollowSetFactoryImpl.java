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
        FollowMapBuilder mapBuilder = new FollowMapBuilder(context, firstMap);
        // 规则一, 标注开始
        HeadDefineSymbol start = mapBuilder.getDefinition(startHead);
        follow1(start, mapBuilder);
        // 规则二
        follow2(start, mapBuilder);
        // 规则三
        follow3(start, mapBuilder);
        return mapBuilder.buildMap();
    }

    private static void follow1(HeadSymbol start, FollowMapBuilder mapBuilder) {
        mapBuilder.getBuilder(start).containsEndMarker = true;
    }

    private void follow2(HeadSymbol start, FollowMapBuilder mapBuilder) {
        Set<HeadSymbol> visited = new HashSet<>();
        mapBuilder.addQueueLast(start);
        while (mapBuilder.queueHasElement()) {
            HeadSymbol headSymbol = mapBuilder.removeQueueFirst();
            if (visited.contains(headSymbol)) {
                continue;
            }
            follow2Head(headSymbol, mapBuilder);
            visited.add(headSymbol);
        }
    }


    private void follow2Head(HeadSymbol head, FollowMapBuilder mapBuilder) {
        GrammarAlternation alternation = mapBuilder.getAlternation(head);
        for (GrammarSymbol symbol : alternation) {
            if (!symbol.isEpsilon()) {
                if (symbol.isConcatenation()) {
                    GrammarConcatenation concatenation = symbol.toConcatenation();
                    follow2(concatenation, mapBuilder);
                } else {
                    throw new IllegalStateException("Unknown type of: " + symbol.getClass());
                }
            }
            // epsilon
            // 不处理
        }
    }

    private void follow2(GrammarConcatenation concatenation, FollowMapBuilder mapBuilder) {
        for (int i = 0; i < concatenation.size(); i++) {
            ConcatenableSymbol symbol = concatenation.get(i);
            requireNotConcatenation(symbol);
            if (symbol.isTerminal()) {
                // 终结符? 不关心
                continue;
            }
            HeadSymbol headSymbol = symbol.toHead();
            mapBuilder.addQueueLast(headSymbol);
            FollowSetBuilder headFollowSetBuilder = mapBuilder.getBuilder(headSymbol);
            cupAssignFirstAfter(headFollowSetBuilder, mapBuilder.firstMap, concatenation, i);
        }
    }

    private void cupAssignFirstAfter(
            FollowSetBuilder builder, FirstMap firstMap, GrammarConcatenation concatenation, int i) {
        HashSet<TerminalSymbol> firstAfter = new HashSet<>();
        for (int j = i + 1; j < concatenation.size(); j++) {
            ConcatenableSymbol symbol = concatenation.get(j);
            requireNotConcatenation(symbol);
            FirstSet firstSet = symbol.isTerminal() ? firstMap.get(symbol.toTerminal()) : firstMap.get(symbol.toHead());
            firstAfter.addAll(firstSet.firstExceptEpsilon());
            if (!firstSet.containsEpsilon()) {
                break;
            }  // 可空则继续
        }
        builder.set.addAll(firstAfter);
    }

    private void follow3(HeadDefineSymbol start, FollowMapBuilder mapBuilder) {
        boolean changed;
        do {
            changed = false;
            Set<HeadSymbol> visited = new HashSet<>();
            mapBuilder.addQueueLast(start);
            while (mapBuilder.queueHasElement()) {
                HeadSymbol headSymbol = mapBuilder.removeQueueFirst();
                if (visited.contains(headSymbol)) {
                    continue;
                }
                changed = follow3(headSymbol, mapBuilder);
                visited.add(headSymbol);
            }
        } while (changed);
    }

    private boolean follow3(HeadSymbol head, FollowMapBuilder mapBuilder) {
        GrammarAlternation alternation = mapBuilder.getAlternation(head);
        FollowSetBuilder headBuilder = mapBuilder.getBuilder(head);
        boolean changed = false;
        for (GrammarSymbol symbol : alternation) {
            if (!symbol.isEpsilon()) {
                if (symbol.isConcatenation()) {
                    GrammarConcatenation concatenation = symbol.toConcatenation();
                    changed = follow3(headBuilder, concatenation, mapBuilder);
                } else {
                    throw new IllegalStateException("Unknown type of: " + symbol.getClass());
                }
            }
            // epsilon
            // 不处理
        }
        return changed;
    }

    private boolean follow3(
            FollowSetBuilder headBuilder, GrammarConcatenation concatenation, FollowMapBuilder mapBuilder) {
        boolean changed = false;
        for (int i = 0; i < concatenation.size(); i++) {
            ConcatenableSymbol symbol = concatenation.get(i);
            requireNotConcatenation(symbol);
            if (symbol.isTerminal()) {
                // 终结符? 不关心
                continue;
            }
            HeadSymbol headSymbol = symbol.toHead();
            mapBuilder.addQueueLast(headSymbol);
            FollowSetBuilder headFollowSetBuilder = mapBuilder.getBuilder(headSymbol);
            if (afterFirstContainsEpsilon(concatenation, mapBuilder.firstMap, i)) {
                changed = cupAssignHeadFollow(headFollowSetBuilder, headBuilder);
            }
        }
        return changed;
    }


    private boolean afterFirstContainsEpsilon(
            GrammarConcatenation concatenation, FirstMap firstMap, int i) {
        for (int j = i + 1; j < concatenation.size(); j++) {
            ConcatenableSymbol symbol = concatenation.get(j);
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

    static class FollowSetBuilder {
        private boolean containsEndMarker = false;
        private final Set<TerminalSymbol> set = new HashSet<>();

        private FollowSet build() {
            return new FollowSetImpl(set, containsEndMarker);
        }
    }

    static class FollowMapBuilder {
        private final ProductionSetContext context;
        private final FirstMap firstMap;
        private final Map<HeadSymbol, FollowSetBuilder> followMap;
        private final LinkedList<HeadSymbol> queue;

        FollowMapBuilder(ProductionSetContext context, FirstMap firstMap) {
            this.context = context;
            this.firstMap = firstMap;
            followMap = new HashMap<>();
            queue = new LinkedList<>();
        }

        public HeadDefineSymbol getDefinition(String startHead) {
            return context.getDefinition(startHead);
        }

        private GrammarAlternation getAlternation(HeadSymbol head) {
            if (!head.isDefine()) {
                throw new IllegalStateException("The head of production is not define head symbol!");
            }
            HeadDefineSymbol define = head.toDefine();
            Integer index = context.indexOf(define);
            if (index == null) {
                throw new IllegalStateException("Can not found define from context!");
            }
            return context.get(index).getBody();
        }

        private FollowSetBuilder getBuilder(HeadSymbol head) {
            return followMap.computeIfAbsent(head, k -> new FollowSetBuilder());
        }

        private FollowMap buildMap() {
            Map<HeadSymbol, FollowSet> collect = followMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
            return new FollowMapImpl(collect);
        }


        HeadSymbol removeQueueFirst() {
            return queue.removeFirst();
        }

        void addQueueLast(HeadSymbol symbol) {
            queue.addLast(symbol);
        }

        boolean queueHasElement() {
            return !queue.isEmpty();
        }
    }
}
