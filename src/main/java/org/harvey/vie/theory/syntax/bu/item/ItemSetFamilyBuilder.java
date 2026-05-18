package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.syntax.grammar.first.FirstMap;
import org.harvey.vie.theory.syntax.grammar.first.FirstSet;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-05 23:32
 */
public class ItemSetFamilyBuilder {
    private final Map<ItemSetBuilder, Integer> family = new HashMap<>();
    private final FirstMap firstMap;
    private IdGenerator idGenerator;
    private ItemSetBuilder[] sorted;

    ItemSetFamilyBuilder(FirstMap firstMap) {
        this.firstMap = firstMap;
    }

    public ItemSetBuilder start() {
        idGenerator = new IdGenerator(0);
        int id = idGenerator.next();
        ItemSetBuilder itemSetBuilder = new ItemSetBuilder(this);
        itemSetBuilder.setIndex(id);
        family.put(itemSetBuilder, itemSetBuilder.getIndex());
        return itemSetBuilder;
    }

    public boolean add(ItemSetBuilder value) {
        Integer id = family.get(value);
        if (id != null) {
            value.setIndex(id);
            return false;
        }
        if (idGenerator == null) {
            throw new IllegalStateException("Add must not be called before constructing start");
        }
        value.setIndex(idGenerator.next());
        family.put(value, value.getIndex());
        return true;
    }

    public FirstSet first(Iterable<GrammarUnitSymbol> afterIterable) {
        return firstMap.first(afterIterable);
    }

    public ItemSetFamily build() {
        this.sorted = family.keySet()
                .stream()
                .sorted(Comparator.comparingInt(ItemSetBuilder::getIndex))
                .toArray(ItemSetBuilder[]::new);
        ItemSet[] array = Arrays.stream(sorted).map(ItemSetBuilder::build).toArray(ItemSet[]::new);
        return new ItemSetFamilyImpl(0, array);
    }

    public ItemSetBuilder getItemSetBuilder(int setId) {
        if (sorted == null) {
            throw new IllegalStateException(
                    "Incorrect call timing: The ItemSetFamily is not yet ready to build, and this call can only be made after the build has been started.");
        }
        return sorted[setId];
    }

    public boolean nullable(HeadSymbol head) {
        return firstMap.nullable(List.of(head));
    }
}
