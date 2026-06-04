package org.harvey.vie.theory.syntax.grammar.follow;

import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.first.FirstSet;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.util.AfterIterable;

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
    /**
     * 函数功能：按第一类规则计算 FOLLOW 集合。
     * 输入：
     * - start：HeadSymbol 类型参数。
     * - mapBuilder：FollowMapBuilder 类型参数。
     * 输出：无。
     */
    private static void follow1(HeadSymbol start, FollowMapBuilder mapBuilder) {
        mapBuilder.getBuilder(start).containsEndMarker = true;
    }
/**
 * 函数功能：获取 FOLLOW 集合。
 * 输入：
 * - startHead：String 类型参数。
 * - context：ProductionSetContext 类型参数。
 * - firstMap：FirstMap 类型参数。
 * 输出：FollowMap 类型返回值。
 */

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
/**
 * 函数功能：按第二类规则计算 FOLLOW 集合。
 * 输入：
 * - start：HeadDefineSymbol 类型参数。
 * - firstMap：FirstMap 类型参数。
 * - mapBuilder：FollowMapBuilder 类型参数。
 * 输出：无。
 */

    private void follow2(HeadDefineSymbol start, FirstMap firstMap, FollowMapBuilder mapBuilder) {
        forEach(start, mapBuilder, (headBuilder, headSymbol, afterIterator) -> {
            FollowSetBuilder headFollowSetBuilder = mapBuilder.getBuilder(headSymbol);
            cupAssignFirstAfter(headFollowSetBuilder, firstMap, afterIterator);
            return true;
        });
    }
/**
 * 函数功能：按第三类规则计算 FOLLOW 集合。
 * 输入：
 * - start：HeadDefineSymbol 类型参数。
 * - firstMap：FirstMap 类型参数。
 * - mapBuilder：FollowMapBuilder 类型参数。
 * 输出：无。
 */

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
/**
 * 函数功能：遍历并执行 FOLLOW 计算规则。
 * 输入：
 * - start：HeadSymbol 类型参数。
 * - mapBuilder：FollowMapBuilder 类型参数。
 * - function：Func 类型参数。
 * 输出：判断结果布尔值。
 */

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
/**
 * 函数功能：遍历产生式并应用 FOLLOW 计算规则。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * - mapBuilder：FollowMapBuilder 类型参数。
 * - queue：HeadQueue 类型参数。
 * - function：Func 类型参数。
 * 输出：判断结果布尔值。
 */

    private boolean forEachProduction(
            HeadSymbol head, FollowMapBuilder mapBuilder, HeadQueue queue, Func function) {
        GrammarAlternation alternation = mapBuilder.getAlternation(head);
        FollowSetBuilder headBuilder = mapBuilder.getBuilder(head);
        boolean changed = false;
        for (AlterableSymbol symbol : alternation) {
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
/**
 * 函数功能：遍历产生式并应用 FOLLOW 计算规则。
 * 输入：
 * - headBuilder：FollowSetBuilder 类型参数。
 * - concatenation：GrammarConcatenation 类型参数。
 * - queue：HeadQueue 类型参数。
 * - function：Func 类型参数。
 * 输出：判断结果布尔值。
 */

    private boolean forEachProduction(
            FollowSetBuilder headBuilder, GrammarConcatenation concatenation, HeadQueue queue, Func function) {
        boolean changed = false;
        for (int i = 0; i < concatenation.size(); i++) {
            GrammarUnitSymbol symbol = concatenation.get(i);
            if (symbol.isTerminal()) {
                // 终结符? 不关心
                continue;
            }
            HeadSymbol headSymbol = symbol.toHead();
            queue.addLast(headSymbol);
            changed = function.invoke(headBuilder, headSymbol, new AfterIterable<>(i, concatenation));
        }
        return changed;
    }
/**
 * 函数功能：将后继 FIRST 集合合并到 FOLLOW 集合。
 * 输入：
 * - builder：FollowSetBuilder 类型参数。
 * - firstMap：FirstMap 类型参数。
 * - afterIterable：AfterIterable<GrammarUnitSymbol> 类型参数。
 * 输出：无。
 */

    private void cupAssignFirstAfter(
            FollowSetBuilder builder,
            FirstMap firstMap,
            AfterIterable<GrammarUnitSymbol> afterIterable) {
        builder.set.addAll(firstMap.first(afterIterable).firstExceptEpsilon());
    }
/**
 * 函数功能：判断后继 FIRST 集合是否包含空串。
 * 输入：
 * - firstMap：FirstMap 类型参数。
 * - afterIterable：AfterIterable<GrammarUnitSymbol> 类型参数。
 * 输出：判断结果布尔值。
 */

    private boolean afterFirstContainsEpsilon(FirstMap firstMap, AfterIterable<GrammarUnitSymbol> afterIterable) {
        for (GrammarUnitSymbol symbol : afterIterable) {

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
/**
 * 函数功能：将头部 FOLLOW 集合合并到目标 FOLLOW 集合。
 * 输入：
 * - builder：FollowSetBuilder 类型参数。
 * - headBuilder：FollowSetBuilder 类型参数。
 * 输出：判断结果布尔值。
 */

    private boolean cupAssignHeadFollow(
            FollowSetBuilder builder, FollowSetBuilder headBuilder) {
        int oldSize = builder.set.size();
        boolean oldContainsEndMarker = builder.containsEndMarker;
        builder.set.addAll(headBuilder.set);
        builder.containsEndMarker = headBuilder.containsEndMarker || builder.containsEndMarker;
        return !(builder.set.size() == oldSize /*size 不同, 就是改变了*/ &&
                 builder.containsEndMarker == oldContainsEndMarker);
    }


    @FunctionalInterface
    private interface Func {
        /**
         * 函数功能：执行 FOLLOW 计算回调。
         * 输入：
         * - headBuilder：FollowSetBuilder 类型参数。
         * - headSymbol：HeadSymbol 类型参数。
         * - afterIterable：AfterIterable<GrammarUnitSymbol> 类型参数。
         * 输出：判断结果布尔值。
         */
        boolean invoke(
                FollowSetBuilder headBuilder,
                HeadSymbol headSymbol,
                AfterIterable<GrammarUnitSymbol> afterIterable);
    }


    private static class HeadQueue {
        private final LinkedList<HeadSymbol> queue = new LinkedList<>();
/**
 * 函数功能：移除首个语法符号。
 * 输入：
 * - 无。
 * 输出：HeadSymbol 类型返回值。
 */

        public HeadSymbol removeFirst() {
            return queue.removeFirst();
        }
/**
 * 函数功能：添加到末尾。
 * 输入：
 * - headSymbol：HeadSymbol 类型参数。
 * 输出：无。
 */

        public void addLast(HeadSymbol headSymbol) {
            queue.addLast(headSymbol);
        }
/**
 * 函数功能：判断是否存在元素。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

        public boolean hasElement() {
            return !queue.isEmpty();
        }
    }

    private static class FollowSetBuilder {
        private final Set<TerminalSymbol> set = new HashSet<>();
        private boolean containsEndMarker = false;
/**
 * 函数功能：构建目标对象。
 * 输入：
 * - 无。
 * 输出：FollowSet 类型返回值。
 */

        public FollowSet build() {
            return new FollowSetImpl(set, containsEndMarker);
        }
    }

    private static class FollowMapBuilder {
        private final ProductionSetContext context;
        private final Map<HeadSymbol, FollowSetBuilder> followMap;
/**
 * 函数功能：创建 FollowMapBuilder 对象。
 * 输入：
 * - context：ProductionSetContext 类型参数。
 * 输出：无。
 */


        public FollowMapBuilder(ProductionSetContext context) {
            this.context = context;
            followMap = new HashMap<>();
        }
/**
 * 函数功能：获取指定名称的非终结符定义。
 * 输入：
 * - startHead：String 类型参数。
 * 输出：HeadDefineSymbol 类型返回值。
 */

        public HeadDefineSymbol getDefinition(String startHead) {
            return context.getDefinition(startHead);
        }
/**
 * 函数功能：获取指定头部符号的候选式集合。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * 输出：GrammarAlternation 类型返回值。
 */

        public GrammarAlternation getAlternation(HeadSymbol head) {
            return context.getAlternation(head);
        }
/**
 * 函数功能：获取指定非终结符的 FIRST 集构建器。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * 输出：FollowSetBuilder 类型返回值。
 */

        public FollowSetBuilder getBuilder(HeadSymbol head) {
            return followMap.computeIfAbsent(head, k -> new FollowSetBuilder());
        }
/**
 * 函数功能：构建映射对象。
 * 输入：
 * - 无。
 * 输出：FollowMap 类型返回值。
 */

        public FollowMap buildMap() {
            Map<HeadSymbol, FollowSet> collect = followMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
            return new FollowMapImpl(collect);
        }

    }
}
