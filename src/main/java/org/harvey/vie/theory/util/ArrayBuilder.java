package org.harvey.vie.theory.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * A generic builder class for dynamically accumulating elements into an array.
 * It provides a more flexible way to build arrays than fixed-size allocation,
 * while maintaining efficiency.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 15:28
 */
public class ArrayBuilder<T> {
    private final List<T> list;
    private int pos;

    public ArrayBuilder() {
        list = new ArrayList<>();
        pos = 0;
    }

    public ArrayBuilder<T> append(T t) {
        if (pos < list.size()) {
            list.set(pos, t);
        } else {
            list.add(t);
        }
        pos++;
        return this;
    }

    public void reset() {
        pos = 0;
    }

    public T[] toArray(IntFunction<T[]> generator) {
        return list.subList(0, pos).toArray(generator);
    }

    public T[] toArray(T[] a) {
        return list.subList(0, pos).toArray(a);
    }

    public List<T> toList(T[] a) {
        return list.subList(0, pos);
    }
}
