package org.harvey.vie.theory.syntax.bu.item;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.syntax.grammar.first.FirstSet;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-05 23:31
 */
public class ItemSetBuilder {
    private final Map<TerminalSymbol, Integer> terminalGoto = new HashMap<>();
    private final Map<HeadSymbol, Integer> headGoto = new HashMap<>();
    private final ItemSetFamilyBuilder familyBuilder;
    @Getter
    private final Set<ProductionItem> set = new HashSet<>();
    @Getter
    @Setter
    private int index = -1;

    ItemSetBuilder(ItemSetFamilyBuilder familyBuilder) {
        this.familyBuilder = familyBuilder;
    }

    public void add(
            ProductionSetContext context,
            Integer productionIndex,
            int size,
            Consumer<ProductionItem> addedConsumer) {
        for (int i = 0; i < size; i++) {
            ProductionItemImpl e = new ProductionItemImpl(context, productionIndex, i, 0);
            int old = set.size();
            set.add(e);
            if (set.size() != old) {
                addedConsumer.accept(e);
            }
        }
    }

    public void add(ProductionItem productionItem) {
        set.add(productionItem);
    }

    public Set<HeadDefineSymbol> nextHeadSet() {
        return set.stream()
                .filter(ProductionItem::hasNextSymbol)
                .map(ProductionItem::nextSymbol)
                .filter(Predicate.not(GrammarSymbol::isTerminal))
                .map(GrammarUnitSymbol::toHead)
                .filter(HeadSymbol::isDefine)
                .map(HeadSymbol::toDefine)
                .collect(Collectors.toSet());
    }

    public void putGoto(GrammarUnitSymbol key, int toSetId) {
        if (key.isTerminal()) {
            TerminalSymbol terminal = key.toTerminal();
            if (terminalGoto.containsKey(terminal)) {
                throw new IllegalStateException("put goto repeatedly");
            }
            terminalGoto.put(terminal, toSetId);
        } else {
            HeadSymbol head = key.toHead();
            if (headGoto.containsKey(head)) {
                throw new IllegalStateException("put goto repeatedly");
            }
            headGoto.put(head, toSetId);
        }
    }

    public ItemSet build() {
        if (index < 0) {
            throw new IllegalStateException(
                    "A Builder that is not assigned an id indicates an unfinished Builder and should not be built");
        }
        // 构造一下dr
        Map<HeadSymbol, Set<TerminalSymbol>> decisionRules = buildDecisionRules();
        return new ItemSetImpl(set, terminalGoto, headGoto, decisionRules);
    }


    private Map<HeadSymbol, Set<TerminalSymbol>> buildDecisionRules() {
        HashMap<HeadSymbol, Set<TerminalSymbol>> decisionRules = new HashMap<>();
        for (ProductionItem item : set) {
            // 2. item: B->alpha · A beta, A 是非终结符
            if (!item.hasNextSymbol()) {
                continue;
            }
            GrammarUnitSymbol unitSymbol = item.nextSymbol();
            if (unitSymbol.isTerminal()) {
                continue;
            }
            FirstSet first = familyBuilder.first(item.afterIterable());
            HeadSymbol nextHead = item.nextSymbol().toHead();
            // dr
            Set<TerminalSymbol> firstExceptEpsilon = first.firstExceptEpsilon();
            if (!firstExceptEpsilon.isEmpty()) {
                decisionRules.computeIfAbsent(nextHead, k -> new HashSet<>()).addAll(firstExceptEpsilon);
            }
        }
        return decisionRules;
    }

    /**
     * Read 只在理论中存在, 用于理解, 当不需要在构造中使用这一集合
     *
     * @deprecated
     */
    @Deprecated
    public Map<HeadSymbol, Set<Integer>> buildRead() {
        HashMap<HeadSymbol, Set<Integer>> readMap = new HashMap<>();
        LinkedList<Integer> queue = new LinkedList<>();
        for (HeadSymbol head : headGoto.keySet()) {
            Integer next = headGoto.get(head);
            Set<Integer> nextSet = readMap.computeIfAbsent(head, k -> new HashSet<>());
            if (nextSet.contains(next)) {
                // 如果nextSet刚创建为空,
                // 则必定不包含next,
                // 则必定会往下走,
                // 则不会存在空nextSet在readMap里
                continue;
            }
            nextSet.add(next);
            queue.addLast(next);
        }
        while (!queue.isEmpty()) {
            Integer first = queue.removeFirst();
            Map<HeadSymbol, Integer> headGoto = familyBuilder.getItemSetBuilder(first).headGoto;
            for (HeadSymbol head : headGoto.keySet()) {
                Integer next = headGoto.get(head);
                if (!familyBuilder.nullable(head)) {
                    continue;
                }
                Set<Integer> nextSet = readMap.computeIfAbsent(head, k -> new HashSet<>());
                if (nextSet.contains(next)) {
                    continue;
                }
                nextSet.add(next);
                queue.addLast(next);
            }
        }
        return readMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemSetBuilder)) {
            return false;
        }
        ItemSetBuilder that = (ItemSetBuilder) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }

}
