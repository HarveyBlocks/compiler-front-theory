package org.harvey.vie.theory.util;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 18:57
 */
public class AfterIterable<T> implements Iterable<T> {
    private final int offset;
    private final IRandomAccess<T> randomAccess;

    /**
     * 函数功能：创建从指定偏移后开始遍历的可迭代对象。
     * 输入：
     * - offset：遍历起始位置之前的偏移量。
     * - randomAccess：提供随机访问能力的数据源。
     * 输出：无。
     */
    public AfterIterable(int offset, IRandomAccess<T> randomAccess) {
        this.offset = offset;
        this.randomAccess = randomAccess;
    }

    /**
     * 函数功能：获取从指定偏移后开始遍历的迭代器。
     * 输入：
     * - 无。
     * 输出：元素迭代器。
     */
    @Override
    public java.util.Iterator<T> iterator() {
        return new Iterator();
    }

    private class Iterator implements java.util.Iterator<T> {
        private int pos = offset + 1;

        /**
         * 函数功能：判断是否还有可遍历的元素。
         * 输入：
         * - 无。
         * 输出：是否存在下一个元素的布尔值。
         */
        @Override
        public boolean hasNext() {
            return pos < randomAccess.size();
        }

        /**
         * 函数功能：获取下一个元素并推进迭代位置。
         * 输入：
         * - 无。
         * 输出：下一个元素。
         */
        @Override
        public T next() {
            return randomAccess.get(pos++);
        }
    }
}
