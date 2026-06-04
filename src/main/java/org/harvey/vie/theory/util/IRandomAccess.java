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

    /**
     * 函数功能：将列表包装为随机访问集合。
     * 输入：
     * - ls：待包装的列表。
     * 输出：列表对应的 IRandomAccess。
     */
    static <E> IRandomAccess<E> of(List<E> ls) {
        return new ListImpl<>(ls);
    }

    /**
     * 函数功能：将数组包装为随机访问集合。
     * 输入：
     * - arr：待包装的数组。
     * 输出：数组对应的 IRandomAccess。
     */
    static <E> IRandomAccess<E> of(E[] arr) {
        return new ArrayImpl<>(arr);
    }

    /**
     * 函数功能：获取空的随机访问集合。
     * 输入：
     * - 无。
     * 输出：空的 IRandomAccess。
     */
    @SuppressWarnings("unchecked")
    static <E> IRandomAccess<E> empty() {
        return (IRandomAccess<E>) EMPTY;
    }

    /**
     * 函数功能：获取指定索引处的元素。
     * 输入：
     * - index：元素索引。
     * 输出：指定索引处的元素。
     */
    E get(int index);

    /**
     * 函数功能：获取从指定索引开始的列表迭代器。
     * 输入：
     * - index：迭代器起始索引。
     * 输出：从指定位置开始的 ListIterator。
     */
    ListIterator<E> listIterator(int index);

    /**
     * 函数功能：获取从起始位置开始的列表迭代器。
     * 输入：
     * - 无。
     * 输出：从起始位置开始的 ListIterator。
     */
    ListIterator<E> listIterator();

    /**
     * 函数功能：获取反向遍历当前集合的迭代器。
     * 输入：
     * - 无。
     * 输出：反向 Iterator。
     */
    default Iterator<E> reverseIterator() {
        return new ReverseIterator<>(this);
    }

    class ReverseIterator<E> implements Iterator<E> {
        private final ListIterator<E> iter;

        /**
         * 函数功能：创建随机访问集合的反向迭代器。
         * 输入：
         * - ra：待反向遍历的随机访问集合。
         * 输出：无。
         */
        public ReverseIterator(IRandomAccess<E> ra) {
            this.iter = ra.listIterator(ra.size());
        }

        /**
         * 函数功能：判断反向迭代器是否还有元素。
         * 输入：
         * - 无。
         * 输出：是否存在下一个反向元素的布尔值。
         */
        @Override
        public boolean hasNext() {
            return iter.hasPrevious();
        }

        /**
         * 函数功能：获取反向迭代中的下一个元素。
         * 输入：
         * - 无。
         * 输出：反向遍历得到的下一个元素。
         */
        @Override
        public E next() {
            return iter.previous();
        }
    }

    class ListImpl<E> implements IRandomAccess<E> {

        protected final List<E> ls;

        /**
         * 函数功能：创建基于列表的随机访问集合。
         * 输入：
         * - ls：作为数据源的列表。
         * 输出：无。
         */
        public ListImpl(List<E> ls) {this.ls = ls;}

        /**
         * 函数功能：获取列表元素迭代器。
         * 输入：
         * - 无。
         * 输出：列表元素 Iterator。
         */
        @Override
        public Iterator<E> iterator() {
            return ls.iterator();
        }

        /**
         * 函数功能：获取列表指定索引处的元素。
         * 输入：
         * - index：元素索引。
         * 输出：指定索引处的元素。
         */
        @Override
        public E get(int index) {
            return ls.get(index);
        }

        /**
         * 函数功能：获取列表从指定索引开始的迭代器。
         * 输入：
         * - index：迭代器起始索引。
         * 输出：从指定位置开始的 ListIterator。
         */
        @Override
        public ListIterator<E> listIterator(int index) {
            return ls.listIterator(index);
        }

        /**
         * 函数功能：获取列表从起始位置开始的迭代器。
         * 输入：
         * - 无。
         * 输出：从起始位置开始的 ListIterator。
         */
        @Override
        public ListIterator<E> listIterator() {
            return ls.listIterator();
        }

        /**
         * 函数功能：获取列表元素数量。
         * 输入：
         * - 无。
         * 输出：元素数量整数。
         */
        @Override
        public int size() {
            return ls.size();
        }
    }

    class ArrayImpl<E> implements IRandomAccess<E> {
        protected final E[] arr;

        /**
         * 函数功能：创建基于数组的随机访问集合。
         * 输入：
         * - arr：作为数据源的数组。
         * 输出：无。
         */
        public ArrayImpl(E[] arr) {this.arr = arr;}

        /**
         * 函数功能：获取数组元素迭代器。
         * 输入：
         * - 无。
         * 输出：数组元素 Iterator。
         */
        @Override
        public Iterator<E> iterator() {
            return Arrays.asList(arr).iterator();
        }

        /**
         * 函数功能：获取数组指定索引处的元素。
         * 输入：
         * - index：元素索引。
         * 输出：指定索引处的元素。
         */
        @Override
        public E get(int index) {
            return Arrays.asList(arr).get(index);
        }

        /**
         * 函数功能：获取数组从指定索引开始的迭代器。
         * 输入：
         * - index：迭代器起始索引。
         * 输出：从指定位置开始的 ListIterator。
         */
        @Override
        public ListIterator<E> listIterator(int index) {
            return Arrays.asList(arr).listIterator(index);
        }

        /**
         * 函数功能：获取数组从起始位置开始的迭代器。
         * 输入：
         * - 无。
         * 输出：从起始位置开始的 ListIterator。
         */
        @Override
        public ListIterator<E> listIterator() {
            return Arrays.asList(arr).listIterator();
        }

        /**
         * 函数功能：获取数组元素数量。
         * 输入：
         * - 无。
         * 输出：元素数量整数。
         */
        @Override
        public int size() {
            return arr.length;
        }
    }

    class EmptyImpl<E> implements IRandomAccess<E> {

        /**
         * 函数功能：获取空集合中指定索引处的元素。
         * 输入：
         * - index：元素索引。
         * 输出：空集合中不存在的元素值。
         */
        @Override
        public E get(int index) {
            Objects.checkIndex(index, 0);
            return null;
        }

        /**
         * 函数功能：获取空集合从指定索引开始的迭代器。
         * 输入：
         * - index：迭代器起始索引。
         * 输出：空的 ListIterator。
         */
        @Override
        public ListIterator<E> listIterator(int index) {
            return Collections.emptyListIterator();
        }

        /**
         * 函数功能：获取空集合从起始位置开始的迭代器。
         * 输入：
         * - 无。
         * 输出：空的 ListIterator。
         */
        @Override
        public ListIterator<E> listIterator() {
            return Collections.emptyListIterator();
        }

        /**
         * 函数功能：获取空集合元素数量。
         * 输入：
         * - 无。
         * 输出：元素数量整数。
         */
        @Override
        public int size() {
            return 0;
        }

        /**
         * 函数功能：获取空集合元素迭代器。
         * 输入：
         * - 无。
         * 输出：空的 Iterator。
         */
        @Override
        public Iterator<E> iterator() {
            return Collections.emptyIterator();
        }
    }
}
