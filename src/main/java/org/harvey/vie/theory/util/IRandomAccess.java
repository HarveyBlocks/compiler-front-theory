package org.harvey.vie.theory.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 18:55
 */
public interface IRandomAccess<E> extends SimpleCollection<E> {
    static <E> IRandomAccess<E> of(List<E> ls) {
        return new IRandomAccess<>() {
            @Override
            public Iterator<E> iterator() {
                return ls.iterator();
            }

            @Override
            public E get(int index) {
                return ls.get(index);
            }

            @Override
            public ListIterator<E> listIterator(int index) {
                return ls.listIterator(index);
            }

            @Override
            public ListIterator<E> listIterator() {
                return ls.listIterator();
            }

            @Override
            public int size() {
                return ls.size();
            }
        };
    }

    E get(int index);

    ListIterator<E> listIterator(int index);

    ListIterator<E> listIterator();

    default Iterator<E> reverseIterator() {
        return new ReverseIterator<>(this);
    }

    class ReverseIterator<E> implements Iterator<E> {
        private final ListIterator<E> iter;

        public ReverseIterator(IRandomAccess<E> ra) {
            this.iter = ra.listIterator(ra.size());
        }

        @Override
        public boolean hasNext() {
            return iter.hasPrevious();
        }

        @Override
        public E next() {
            return iter.previous();
        }
    }
}
