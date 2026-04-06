package org.harvey.vie.theory.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 15:42
 */
public interface SimpleCollection<T> extends Iterable<T> {
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }
}
