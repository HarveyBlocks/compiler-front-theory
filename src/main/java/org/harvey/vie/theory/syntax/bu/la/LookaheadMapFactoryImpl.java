package org.harvey.vie.theory.syntax.bu.la;

import lombok.Getter;
import org.harvey.vie.theory.syntax.bu.item.ItemSet;
import org.harvey.vie.theory.syntax.bu.item.ItemSetFamily;
import org.harvey.vie.theory.syntax.bu.item.ProductionItem;
import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 15:17
 */
public class LookaheadMapFactoryImpl implements LookaheadMapFactory {
    /**
     * 函数功能：初始化展望符映射。
     * 输入：
     * - itemSetIndex：int 类型参数。
     * - item：ProductionItem 类型参数。
     * - lsc：LookaheadSetContext 类型参数。
     * 输出：无。
     */
    private static void initLookahead(
            int itemSetIndex, ProductionItem item, LookaheadSetContext lsc) {
        if (item.currentDot() == 0) {
            // 规则2, 可以处理 ε
            Set<TerminalSymbol> terminalSet = new HashSet<>(lsc.decisionRule(itemSetIndex, item.getHead()));
            if (lsc.startDefinition().equals(item.getHead())) {
                // 是规则 2.1
                terminalSet.add(TerminalSymbol.END_MARK_SYMBOL);
            }
            // 规则 2.2, 可以处理 ε
            lsc.addAll(lsc.getLookaheadSet(itemSetIndex, item), terminalSet);
        }
    }

    /**
     * 函数功能：设置内部展望符传播关系。
     * 输入：
     * - itemSetIndex：int 类型参数。
     * - nextSymbol：GrammarUnitSymbol 类型参数。
     * - p：ProductionItem 类型参数。
     * - lsc：LookaheadSetContext 类型参数。
     * 输出：无。
     */

    private static void setInnerPropagation(
            int itemSetIndex, GrammarUnitSymbol nextSymbol, ProductionItem p, LookaheadSetContext lsc) {
        if (nextSymbol.isTerminal()) {
            return;
        }
        HeadSymbol nextSymbolHead = nextSymbol.toHead();
        if (!lsc.nullable(p.afterIterable())) {
            return;
        }
        // 规则 3.1, 集合内传播
        LookaheadSet lookaheadFrom = lsc.getLookaheadSet(itemSetIndex, p);
        lsc.getItemSet(itemSetIndex)
                .stream()
                // 形如 B->·γ (q) 的
                .filter(q -> q.currentDot() == 0)
                .filter(q -> nextSymbolHead.equals(q.getHead()))
                .map(q -> lsc.getLookaheadSet(itemSetIndex, q))
                .forEach(lookaheadTo -> {
                    // 构造 p -> q
                    lsc.buildEdge(lookaheadFrom, lookaheadTo);
                    lsc.propagation(lookaheadFrom, lookaheadTo);
                });
    }

    /**
     * 函数功能：设置外部展望符传播关系。
     * 输入：
     * - itemSetIndex：int 类型参数。
     * - nextSymbol：GrammarUnitSymbol 类型参数。
     * - p：ProductionItem 类型参数。
     * - lsc：LookaheadSetContext 类型参数。
     * 输出：无。
     */

    private static void setOuterPropagation(
            int itemSetIndex, GrammarUnitSymbol nextSymbol, ProductionItem p, LookaheadSetContext lsc) {
        // 规则 3.2
        // B是任意符号(不包括ε).
        int outerSetIndex = lsc.gotoUnit(itemSetIndex, nextSymbol);
        if (outerSetIndex == ItemSet.NONE) {
            return;
        }
        //  若 GOTO[I,B]=J 存在
        LookaheadSet lookaheadFrom = lsc.getLookaheadSet(itemSetIndex, p);
        lsc.getItemSet(outerSetIndex)
                .stream()
                // 对于 J 的每一个形如 A-> αB·β (q)
                .filter(p::productionEquals)
                .filter(ProductionItem::hasPreviousSymbol)
                .filter(q -> nextSymbol.equals(q.previousSymbol()))
                .map(q -> lsc.getLookaheadSet(outerSetIndex, q))
                .forEach(lookaheadTo -> {
                    // 添加传播边, 构造 p -> q
                    lsc.buildEdge(lookaheadFrom, lookaheadTo);
                    lsc.propagation(lookaheadFrom, lookaheadTo);
                });
    }

    /**
     * 函数功能：根据输入数据创建目标对象。
     * 输入：
     * - startHead：String 类型参数。
     * - psc：ProductionSetContext 类型参数。
     * - family：ItemSetFamily 类型参数。
     * - firstMap：FirstMap 类型参数。
     * 输出：LookaheadMap[] 类型数组。
     */

    @Override
    public LookaheadMap[] produce(
            String startHead, ProductionSetContext psc, ItemSetFamily family, FirstMap firstMap) {
        // 1. 每个item的LA = {}
        LookaheadSetContext lsc = new LookaheadSetContext(startHead, psc, family, firstMap);
        for (int i = 0; i < lsc.familySize(); i++) {
            ItemSet set = lsc.getItemSet(i);
            for (ProductionItem item : set) {
                initLookahead(i, item, lsc);
                if (item.isEpsilon()) {
                    continue;
                }
                // 规则 3, 不处理 ε
                if (!item.hasNextSymbol()) {
                    // 如果不包含下一个了, 就跳过
                    continue;
                }
                GrammarUnitSymbol nextSymbol = item.nextSymbol();
                setInnerPropagation(i, nextSymbol, item, lsc);
                setOuterPropagation(i, nextSymbol, item, lsc);
            }
        }
        // 5. 循环直到不动点
        return lsc.filter();
    }

    @Getter
    private static class LookaheadNode {
        private final LookaheadSet lookahead;
        private final Map<LookaheadSet, LookaheadNode> next = new HashMap<>();

        /**
         * 函数功能：创建 LookaheadNode 对象。
         * 输入：
         * - lookahead：LookaheadSet 类型参数。
         * 输出：无。
         */

        private LookaheadNode(LookaheadSet lookahead) {
            this.lookahead = lookahead;
        }

        /**
         * 函数功能：添加全部元素。
         * 输入：
         * - set：Collection<TerminalSymbol> 类型参数。
         * 输出：Collection<TerminalSymbol> 类型集合或迭代结果。
         */

        public Collection<TerminalSymbol> addAll(Collection<TerminalSymbol> set) {
            return lookahead.addAll(set);
        }

        /**
         * 函数功能：写入或合并指定集合。
         * 输入：
         * - lookahead：LookaheadSet 类型参数。
         * - node：LookaheadNode 类型参数。
         * 输出：无。
         */

        public void put(LookaheadSet lookahead, LookaheadNode node) {
            next.put(lookahead, node);
        }

        /**
         * 函数功能：计算项目集的后继集合。
         * 输入：
         * - 无。
         * 输出：Iterable<LookaheadSet> 类型集合或迭代结果。
         */

        public Iterable<LookaheadSet> nextSets() {
            return getNext().keySet();
        }
    }

    private static class LookaheadMapBuilder {
        private final Map<ProductionItem, LookaheadSet> map = new HashMap<>();

        /**
         * 函数功能：创建 LookaheadMapBuilder 对象。
         * 输入：
         * - 无。
         * 输出：无。
         */

        private LookaheadMapBuilder() {}

        /**
         * 函数功能：获取指定键对应集合，缺失时创建。
         * 输入：
         * - item：ProductionItem 类型参数。
         * - mappingFunction：Function<? super ProductionItem, ? extends LookaheadSet> 类型参数。
         * 输出：LookaheadSet 类型返回值。
         */

        public LookaheadSet computeIfAbsent(
                ProductionItem item, Function<? super ProductionItem, ? extends LookaheadSet> mappingFunction) {
            return map.computeIfAbsent(item, mappingFunction);
        }

        /**
         * 函数功能：判断当前对象是否为空。
         * 输入：
         * - 无。
         * 输出：判断结果布尔值。
         */

        public boolean isEmpty() {
            return map.isEmpty();
        }

        /**
         * 函数功能：构建目标对象。
         * 输入：
         * - 无。
         * 输出：Set<TerminalSymbol>> 类型集合或迭代结果。
         */

        public Map<ProductionItem, Set<TerminalSymbol>> build() {
            return Optional.of(map)
                    .map(m -> m.values()
                            .stream()
                            .filter(LookaheadSet::usable)
                            .collect(Collectors.toMap(la -> la.item, la -> la.lookahead)))
                    .filter(Predicate.not(Map::isEmpty))
                    .orElse(null);
        }
    }

    private static class LookaheadSetContext {
        private final Map<LookaheadSet, LookaheadNode> nodeMap = new HashMap<>();
        private final LookaheadMapBuilder[] mapArray;
        private final ItemSetFamily family;
        private final FirstMap firstMap;
        private final String startHead;
        private final ProductionSetContext psc;

        /**
         * 函数功能：创建 LookaheadSetContext 对象。
         * 输入：
         * - startHead：String 类型参数。
         * - psc：ProductionSetContext 类型参数。
         * - family：ItemSetFamily 类型参数。
         * - firstMap：FirstMap 类型参数。
         * 输出：无。
         */

        private LookaheadSetContext(
                String startHead, ProductionSetContext psc, ItemSetFamily family, FirstMap firstMap) {
            this.startHead = startHead;
            this.psc = psc;
            this.family = family;
            this.mapArray = new LookaheadMapBuilder[family.size()];
            this.firstMap = firstMap;
        }

        /**
         * 函数功能：获取项目的展望符集合。
         * 输入：
         * - setIndex：int 类型参数。
         * 输出：LookaheadMapBuilder 类型返回值。
         */

        private LookaheadMapBuilder getLookaheadSet(int setIndex) {
            LookaheadMapBuilder map = mapArray[setIndex];
            if (map == null) {
                mapArray[setIndex] = map = new LookaheadMapBuilder();
            }
            return map;
        }

        /**
         * 函数功能：获取项目的展望符集合。
         * 输入：
         * - setIndex：int 类型参数。
         * - item：ProductionItem 类型参数。
         * 输出：LookaheadSet 类型返回值。
         */

        private LookaheadSet getLookaheadSet(int setIndex, ProductionItem item) {
            return getLookaheadSet(setIndex).computeIfAbsent(item, k -> new LookaheadSet(k, setIndex));
        }

        /**
         * 函数功能：添加全部元素。
         * 输入：
         * - lookahead：LookaheadSet 类型参数。
         * - terminalSet：Collection<TerminalSymbol> 类型参数。
         * 输出：无。
         */

        public void addAll(LookaheadSet lookahead, Collection<TerminalSymbol> terminalSet) {
            if (terminalSet.isEmpty()) { // 即使形成了环, 也会使terminalSet逐渐变少, 最终停止递归
                return;
            }
            LookaheadNode lookaheadNode = getNode(lookahead);
            Collection<TerminalSymbol> trueAdded = lookaheadNode.addAll(terminalSet);
            if (trueAdded.isEmpty()) {
                return;
            }
            for (LookaheadSet next : lookaheadNode.nextSets()) {
                addAll(next, trueAdded);
            }
        }

        /**
         * 函数功能：构建展望符传播边。
         * 输入：
         * - from：LookaheadSet 类型参数。
         * - to：LookaheadSet 类型参数。
         * 输出：无。
         */

        public void buildEdge(LookaheadSet from, LookaheadSet to) {
            LookaheadNode fromNode = getNode(from);
            LookaheadNode toNode = getNode(to);
            fromNode.put(to, toNode);
        }

        /**
         * 函数功能：传播展望符集合。
         * 输入：
         * - from：LookaheadSet 类型参数。
         * - to：LookaheadSet 类型参数。
         * 输出：无。
         */

        public void propagation(LookaheadSet from, LookaheadSet to) {
            addAll(to, from.getLookahead());
        }

        /**
         * 函数功能：获取指定项目的传播节点。
         * 输入：
         * - lookahead：LookaheadSet 类型参数。
         * 输出：LookaheadNode 类型返回值。
         */

        private LookaheadNode getNode(LookaheadSet lookahead) {
            return nodeMap.computeIfAbsent(lookahead, LookaheadNode::new);
        }

        /**
         * 函数功能：获取起始定义符号。
         * 输入：
         * - 无。
         * 输出：HeadDefineSymbol 类型返回值。
         */


        public HeadDefineSymbol startDefinition() {
            return psc.getDefinition(startHead);
        }

        /**
         * 函数功能：获取指定项目集。
         * 输入：
         * - i：int 类型参数。
         * 输出：ItemSet 类型返回值。
         */

        public ItemSet getItemSet(int i) {
            return family.get(i);
        }

        /**
         * 函数功能：获取项目集族大小。
         * 输入：
         * - 无。
         * 输出：整数结果。
         */

        public int familySize() {
            return family.size();
        }

        /**
         * 函数功能：获取指定非终结符的决策规则。
         * 输入：
         * - i：int 类型参数。
         * - head：HeadSymbol 类型参数。
         * 输出：Set<TerminalSymbol> 类型集合或迭代结果。
         */

        public Set<TerminalSymbol> decisionRule(int i, HeadSymbol head) {
            return family.get(i).decisionRule(head);
        }

        /**
         * 函数功能：获取指定语法符号的转移目标。
         * 输入：
         * - i：int 类型参数。
         * - nextSymbol：GrammarUnitSymbol 类型参数。
         * 输出：整数结果。
         */

        public int gotoUnit(int i, GrammarUnitSymbol nextSymbol) {
            return family.get(i).gotoUnit(nextSymbol);
        }

        /**
         * 函数功能：判断指定符号是否可推出空串。
         * 输入：
         * - iterable：Iterable<GrammarUnitSymbol> 类型参数。
         * 输出：判断结果布尔值。
         */

        public boolean nullable(Iterable<GrammarUnitSymbol> iterable) {
            return firstMap.nullable(iterable);
        }

        /**
         * 函数功能：筛选满足条件的项目。
         * 输入：
         * - 无。
         * 输出：LookaheadMap[] 类型数组。
         */

        public LookaheadMap[] filter() {
            return Arrays.stream(mapArray)
                    .map(Optional::ofNullable)
                    .map(op -> op.map(LookaheadMapBuilder::build))
                    .map(op -> op.map(LookaheadMapImpl::new))
                    .map(op -> op.orElse(null))
                    .toArray(LookaheadMap[]::new);
        }
    }

    private static class LookaheadSet {
        private final ProductionItem item;
        private final int setIndex;
        @Getter
        private final Set<TerminalSymbol> lookahead = new HashSet<>();

        /**
         * 函数功能：创建 LookaheadSet 对象。
         * 输入：
         * - item：ProductionItem 类型参数。
         * - setIndex：int 类型参数。
         * 输出：无。
         */

        private LookaheadSet(ProductionItem item, int setIndex) {
            this.item = item;
            this.setIndex = setIndex;
        }

        /**
         * 函数功能：添加全部元素。
         * 输入：
         * - add：Collection<TerminalSymbol> 类型参数。
         * 输出：Collection<TerminalSymbol> 类型集合或迭代结果。
         */

        public Collection<TerminalSymbol> addAll(Collection<TerminalSymbol> add) {
            Set<TerminalSymbol> trueAdded = new HashSet<>();
            for (TerminalSymbol t : add) {
                if (lookahead.contains(t)) {
                    continue;
                }
                lookahead.add(t);
                trueAdded.add(t);
            }
            return trueAdded;
        }

        /**
         * 函数功能：判断当前对象是否与指定对象相等。
         * 输入：
         * - o：Object 类型参数。
         * 输出：判断结果布尔值。
         */

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof LookaheadSet)) {
                return false;
            }
            LookaheadSet that = (LookaheadSet) o;
            return setIndex == that.setIndex && Objects.equals(item, that.item);
        }

        /**
         * 函数功能：返回当前对象的哈希值。
         * 输入：
         * - 无。
         * 输出：整数结果。
         */

        @Override
        public int hashCode() {
            return Objects.hash(item, setIndex);
        }

        /**
         * 函数功能：判断项目是否可用于传播。
         * 输入：
         * - 无。
         * 输出：判断结果布尔值。
         */

        public boolean usable() {
            return !item.hasNextSymbol(); // 没有下一个了才是usable
        }

        /**
         * 函数功能：返回当前对象的字符串表示。
         * 输入：
         * - 无。
         * 输出：字符串结果。
         */

        @Override
        public String toString() {
            return "LA[I" + setIndex + ",`" + item + "`]=" + lookahead;
        }
    }
}
