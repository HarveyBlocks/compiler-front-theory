package org.harvey.vie.theory.syntax.bu.item;

import java.util.Arrays;
import java.util.Iterator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 17:29
 */
class ItemSetFamilyImpl implements ItemSetFamily {
    private final ItemSet[] array;
    private final int startIndex;

    public ItemSetFamilyImpl(int startIndex, ItemSet[] array) {
        this.array = array;
        this.startIndex = startIndex;
    }

    @Override
    public ItemSet get(int i) {
        return array[i];
    }

    @Override
    public int startIndex() {
        return startIndex;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public Iterator<ItemSet> iterator() {
        return Arrays.stream(array).iterator();
    }
}
