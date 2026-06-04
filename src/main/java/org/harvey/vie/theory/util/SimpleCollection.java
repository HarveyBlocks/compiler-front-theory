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
    /**
     * 函数功能：将集合转换为顺序流。
     * 输入：
     * - 无。
     * 输出：集合元素的 Stream。
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * 函数功能：获取集合元素数量。
     * 输入：
     * - 无。
     * 输出：元素数量整数。
     */
    int size();

    /**
     * 函数功能：判断集合是否为空。
     * 输入：
     * - 无。
     * 输出：是否为空的布尔值。
     */
    default boolean isEmpty() {
        return size() == 0;
    }
}
