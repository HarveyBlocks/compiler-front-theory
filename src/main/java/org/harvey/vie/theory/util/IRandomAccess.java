package org.harvey.vie.theory.util;

import java.util.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 18:55
 */
public interface IRandomAccess<E> extends SimpleCollection<E> {
    @SuppressWarnings("rawtypes")
    IRandomAccess EMPTY = new EmptyImpl<>();

    static <E> IRandomAccess<E> of(List<E> ls) {
        return new ListImpl<>(ls);
    }

    static <E> IRandomAccess<E> of(E[] arr) {
        return new ArrayImpl<>(arr);
    }

    @SuppressWarnings("unchecked")
    static <E> IRandomAccess<E> empty() {
        return (IRandomAccess<E>) EMPTY;
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

    class ListImpl<E> implements IRandomAccess<E> {

        protected final List<E> ls;

        public ListImpl(List<E> ls) {this.ls = ls;}

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
    }

    class ArrayImpl<E> implements IRandomAccess<E> {
        protected final E[] arr;

        public ArrayImpl(E[] arr) {this.arr = arr;}

        @Override
        public Iterator<E> iterator() {
            return Arrays.asList(arr).iterator();
        }

        @Override
        public E get(int index) {
            return Arrays.asList(arr).get(index);
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return Arrays.asList(arr).listIterator(index);
        }

        @Override
        public ListIterator<E> listIterator() {
            return Arrays.asList(arr).listIterator();
        }

        @Override
        public int size() {
            return arr.length;
        }
    }

    class EmptyImpl<E> implements IRandomAccess<E> {

        @Override
        public E get(int index) {
            Objects.checkIndex(index, 0);
            return null;
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return Collections.emptyListIterator();
        }

        @Override
        public ListIterator<E> listIterator() {
            return Collections.emptyListIterator();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Iterator<E> iterator() {
            return Collections.emptyIterator();
        }
    }
}
