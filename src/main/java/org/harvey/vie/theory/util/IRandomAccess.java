package org.harvey.vie.theory.util;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 18:55
 */
public interface IRandomAccess<T> {
    T get(int index);

    int size();

    static <T> IRandomAccess<T> of(List<T> ls) {
        return new IRandomAccess<T>() {
            @Override
            public T get(int index) {
                return ls.get(index);
            }

            @Override
            public int size() {
                return ls.size();
            }
        };
    }
}
