package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;

import java.util.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 23:40
 */
public class ItemSetFamilyFactoryImpl implements ItemSetFamilyFactory {


    @Override
    public ItemSetFamily produce(String startHead, ProductionSetContext context, FirstMap firstMap) {
        ItemSetFamilyBuilder familyBuilder = new ItemSetFamilyBuilder(firstMap);
        // 1. start
        // 2. start closure->set
        ItemSetBuilder startBuilder = familyBuilder.start();
        closure(startBuilder, Set.of(context.getDefinition(startHead)), context);
        LinkedList<ItemSetBuilder> queue = new LinkedList<>();
        queue.addLast(startBuilder);
        while (!queue.isEmpty()) {
            // 3. set-gotoEach->map<unit,set>
            ItemSetBuilder first = queue.removeFirst();
            Map<GrammarUnitSymbol, ItemSetBuilder> map = gotoEach(first, familyBuilder);
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

    private Map<GrammarUnitSymbol, ItemSetBuilder> gotoEach(ItemSetBuilder origin, ItemSetFamilyBuilder familyBuilder) {
        Map<GrammarUnitSymbol, ItemSetBuilder> map = new HashMap<>();
        for (ProductionItem productionItem : origin.getSet()) {
            if (productionItem.hasNextSymbol()) {
                GrammarUnitSymbol nextSymbol = productionItem.nextSymbol();
                ItemSetBuilder setBuilder = map.computeIfAbsent(nextSymbol, k -> new ItemSetBuilder(familyBuilder));
                setBuilder.add(productionItem.nextItem());
            }
        }

        return map;
    }

    private void filter(
            ItemSetBuilder fromSet,
            Map<GrammarUnitSymbol, ItemSetBuilder> toMap,
            ItemSetFamilyBuilder familyBuilder,
            LinkedList<ItemSetBuilder> queue) {
        // [set,unit]->set
        for (Map.Entry<GrammarUnitSymbol, ItemSetBuilder> entry : toMap.entrySet()) {
            ItemSetBuilder toSet = entry.getValue();
            boolean added = familyBuilder.add(toSet);
            if (added) {
                queue.addLast(toSet);
            }
            fromSet.putGoto(entry.getKey(), toSet.getIndex());
        }

    }

}
