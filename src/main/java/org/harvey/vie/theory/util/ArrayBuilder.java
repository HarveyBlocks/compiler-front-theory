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

    /**
     * 函数功能：创建空的数组构建器。
     * 输入：
     * - 无。
     * 输出：无。
     */
    public ArrayBuilder() {
        list = new ArrayList<>();
        pos = 0;
    }

    /**
     * 函数功能：向构建器追加一个元素。
     * 输入：
     * - t：待追加的元素。
     * 输出：当前 ArrayBuilder 实例。
     */
    public ArrayBuilder<T> append(T t) {
        if (pos < list.size()) {
            list.set(pos, t);
        } else {
            list.add(t);
        }
        pos++;
        return this;
    }

    /**
     * 函数功能：重置构建器的写入位置。
     * 输入：
     * - 无。
     * 输出：无。
     */
    public void reset() {
        pos = 0;
    }

    /**
     * 函数功能：将当前元素转换为指定生成器创建的数组。
     * 输入：
     * - generator：用于创建目标数组的生成器。
     * 输出：包含当前元素的数组。
     */
    public T[] toArray(IntFunction<T[]> generator) {
        return list.subList(0, pos).toArray(generator);
    }

    /**
     * 函数功能：将当前元素转换为指定数组类型的数组。
     * 输入：
     * - a：接收元素的目标数组。
     * 输出：包含当前元素的数组。
     */
    public T[] toArray(T[] a) {
        return list.subList(0, pos).toArray(a);
    }

    /**
     * 函数功能：将当前元素转换为列表视图。
     * 输入：
     * - a：用于指示元素类型的数组参数。
     * 输出：包含当前元素的 List。
     */
    public List<T> toList(T[] a) {
        return list.subList(0, pos);
    }

    /**
     * 函数功能：判断构建器当前是否为空。
     * 输入：
     * - 无。
     * 输出：是否为空的布尔值。
     */
    public boolean isEmpty() {
        return pos == 0;
    }
}
