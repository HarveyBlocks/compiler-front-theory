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
     * <ol>
     *     <li>每个item的LA = {}</li>
     *     <li>
     *         初始化规则
     *         <ul>
     *             <li>形如 S'->·S 的 Item, 加入 $</li>
     *             <li>对于每个 ItemSet 中的每个 Item, 形如 B -> ·γ(包括ε). LA += DR[ItemSet,B]</li>
     *             <li>上述两条规则可以同时生效</li>
     *         </ul>
     *     </li>
     *     <li>
     *         对于每个 ItemSet 中的每个 Item, 形如 A -> α·Bβ (p) 的
     *         <ul>
     *             <li>
     *                 B是non-terminal. 若 β =>* ε <br>
     *                 则对于 Item 从属的 ItemSet 的每一个形如 B->·γ (q), 添加传播边 p->q
     *             </li>
     *             <li>
     *                 B是任意符号(不包括ε). 若 GOTO[I,B]=J 存在 <br>
     *                 则对于 J 的每一个形如 A-> αB·β (q), 添加传播边 p->q
     *             </li>
     *             <li>上述两条的α和β可以是ε</li>
     *             <li>上述两条规则可以同时生效</li>
     *         </ul>
     *     </li>
     *     <li>建立了 o->p->q, 就是 p.la.addAll(o.la), 然后向下传播, q.la.addAll(p.la)</li>
     *     <li>循环直到不动点</li>
     *     <li>最终只保留形如 A->γ·的产生式的LA集合, 因为LA[I, A->γ·]才会被后续使用</li>
     * </ol>
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

        private LookaheadNode(LookaheadSet lookahead) {
            this.lookahead = lookahead;
        }

        public Collection<TerminalSymbol> addAll(Collection<TerminalSymbol> set) {
            return lookahead.addAll(set);
        }

        public void put(LookaheadSet lookahead, LookaheadNode node) {
            next.put(lookahead, node);
        }

        public Iterable<LookaheadSet> nextSets() {
            return getNext().keySet();
        }
    }

    private static class LookaheadMapBuilder {
        private final Map<ProductionItem, LookaheadSet> map = new HashMap<>();

        private LookaheadMapBuilder() {}

        public LookaheadSet computeIfAbsent(
                ProductionItem item, Function<? super ProductionItem, ? extends LookaheadSet> mappingFunction) {
            return map.computeIfAbsent(item, mappingFunction);
        }

        public boolean isEmpty() {
            return map.isEmpty();
        }

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

        private LookaheadSetContext(
                String startHead, ProductionSetContext psc, ItemSetFamily family, FirstMap firstMap) {
            this.startHead = startHead;
            this.psc = psc;
            this.family = family;
            this.mapArray = new LookaheadMapBuilder[family.size()];
            this.firstMap = firstMap;
        }

        private LookaheadMapBuilder getLookaheadSet(int setIndex) {
            LookaheadMapBuilder map = mapArray[setIndex];
            if (map == null) {
                mapArray[setIndex] = map = new LookaheadMapBuilder();
            }
            return map;
        }

        private LookaheadSet getLookaheadSet(int setIndex, ProductionItem item) {
            return getLookaheadSet(setIndex).computeIfAbsent(item, k -> new LookaheadSet(k, setIndex));
        }

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

        public void buildEdge(LookaheadSet from, LookaheadSet to) {
            LookaheadNode fromNode = getNode(from);
            LookaheadNode toNode = getNode(to);
            fromNode.put(to, toNode);
        }

        public void propagation(LookaheadSet from, LookaheadSet to) {
            addAll(to, from.getLookahead());
        }

        private LookaheadNode getNode(LookaheadSet lookahead) {
            return nodeMap.computeIfAbsent(lookahead, LookaheadNode::new);
        }


        public HeadDefineSymbol startDefinition() {
            return psc.getDefinition(startHead);
        }

        public ItemSet getItemSet(int i) {
            return family.get(i);
        }

        public int familySize() {
            return family.size();
        }

        public Set<TerminalSymbol> decisionRule(int i, HeadSymbol head) {
            return family.get(i).decisionRule(head);
        }

        public int gotoUnit(int i, GrammarUnitSymbol nextSymbol) {
            return family.get(i).gotoUnit(nextSymbol);
        }

        public boolean nullable(Iterable<GrammarUnitSymbol> iterable) {
            return firstMap.nullable(iterable);
        }

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

        private LookaheadSet(ProductionItem item, int setIndex) {
            this.item = item;
            this.setIndex = setIndex;
        }

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

        @Override
        public int hashCode() {
            return Objects.hash(item, setIndex);
        }

        public boolean usable() {
            return !item.hasNextSymbol(); // 没有下一个了才是usable
        }

        @Override
        public String toString() {
            return "LA[I" + setIndex + ",`" + item + "`]=" + lookahead;
        }
    }
}
