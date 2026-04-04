package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 23:40
 */
public class ItemSetFamilyFactoryImpl implements ItemSetFamilyFactory {

    @Override
    public ItemSetFamily produce(String startHead, ProductionSetContext context) {
        ItemSetFamilyBuilder familyBuilder = new ItemSetFamilyBuilder();
        // 1. start
        // 2. start closure->set
        ItemSetBuilder startBuilder = familyBuilder.start();
        closure(startBuilder, Set.of(context.getDefinition(startHead)), context);
        LinkedList<ItemSetBuilder> queue = new LinkedList<>();
        queue.addLast(startBuilder);
        while (!queue.isEmpty()) {
            // 3. set-gotoEach->map<unit,set>
            ItemSetBuilder first = queue.removeFirst();
            Map<GrammarUnitSymbol, ItemSetBuilder> map = gotoEach(first);
            // 4. closure all
            closureAll(map, context);
            // 5. filter->many set
            filter(first, map, familyBuilder, queue);
        }
        return familyBuilder.build();
    }

    private void closure(ItemSetBuilder setBuilder, Set<HeadDefineSymbol> nextHeadSet, ProductionSetContext context) {
        // 只对next进行增加
        LinkedList<HeadDefineSymbol> queue = new LinkedList<>(nextHeadSet);
        Set<HeadDefineSymbol> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            HeadDefineSymbol first = queue.removeFirst();
            if (visited.contains(first)) {
                continue;
            }
            visited.add(first);
            Integer productionIndex = context.indexOf(first);
            GrammarAlternation alternation = context.getAlternation(first);
            setBuilder.add(context, productionIndex, alternation.size(), e -> {
                if (e.hasNextSymbol()) {
                    GrammarUnitSymbol unitSymbol = e.nextSymbol();
                    if (!unitSymbol.isTerminal()) {
                        // head
                        queue.add(unitSymbol.toHead().toDefine());
                    }
                }
            });
        }

    }

    private void closureAll(Map<GrammarUnitSymbol, ItemSetBuilder> map, ProductionSetContext context) {
        map.values().forEach(e -> closure(e, e.nextHeadSet(), context));
    }

    private Map<GrammarUnitSymbol, ItemSetBuilder> gotoEach(ItemSetBuilder origin) {
        Map<GrammarUnitSymbol, ItemSetBuilder> map = new HashMap<>();
        for (ProductionItem productionItem : origin.set) {
            if (productionItem.hasNextSymbol()) {
                GrammarUnitSymbol nextSymbol = productionItem.nextSymbol();
                ItemSetBuilder setBuilder = map.computeIfAbsent(nextSymbol, k -> new ItemSetBuilder());
                setBuilder.add(productionItem.nextItem());
            }
        }

        return map;
    }

    private void filter(
            ItemSetBuilder fromSet, Map<GrammarUnitSymbol, ItemSetBuilder> toMap,
            ItemSetFamilyBuilder familyBuilder,
            LinkedList<ItemSetBuilder> queue) {
        // [set,unit]->set
        for (Map.Entry<GrammarUnitSymbol, ItemSetBuilder> entry : toMap.entrySet()) {
            ItemSetBuilder toSet = entry.getValue();
            boolean added = familyBuilder.add(toSet);
            if (added) {
                queue.addLast(toSet);
            }
            fromSet.putGoto(entry.getKey(), toSet.id);
        }

    }

    private static class ItemSetBuilder {
        private int id = -1;
        private final Set<ProductionItem> set = new HashSet<>();
        private final Map<TerminalSymbol, Integer> terminalGoto = new HashMap<>();
        private final Map<HeadSymbol, Integer> headGoto = new HashMap<>();

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
                    .filter(u -> !u.isTerminal())
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
            if (id < 0) {
                throw new IllegalStateException(
                        "A Builder that is not assigned an id indicates an unfinished Builder and should not be built");
            }
            return new ItemSetImpl(set, terminalGoto, headGoto);
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

    private static class ItemSetFamilyBuilder {
        private final Map<ItemSetBuilder, Integer> family = new HashMap<>();
        private IdGenerator idGenerator;

        public ItemSetBuilder start() {
            idGenerator = new IdGenerator(0);
            int id = idGenerator.next();
            ItemSetBuilder itemSetBuilder = new ItemSetBuilder();
            itemSetBuilder.id = id;
            family.put(itemSetBuilder, itemSetBuilder.id);
            return itemSetBuilder;
        }

        public boolean add(ItemSetBuilder value) {
            Integer id = family.get(value);
            if (id != null) {
                value.id = id;
                return false;
            }
            if (idGenerator == null) {
                throw new IllegalStateException("Add must not be called before constructing start");
            }
            value.id = idGenerator.next();
            family.put(value, value.id);
            return true;
        }

        public ItemSetFamily build() {
            ItemSet[] array = family.keySet().stream()
                    .sorted(Comparator.comparingInt(o -> o.id))
                    .map(ItemSetBuilder::build)
                    .toArray(ItemSet[]::new);
            return new ItemSetFamilyImpl(0, array);
        }

    }

}
