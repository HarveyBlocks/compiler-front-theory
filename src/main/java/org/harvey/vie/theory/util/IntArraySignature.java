package org.harvey.vie.theory.util;

import java.util.Arrays;

/**
 * A wrapper for an integer array that provides stable {@link #hashCode()}
 * and {@link #equals(Object)} implementations. This is particularly useful
 * when using integer arrays as keys in a hash-based collection.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 23:51
 */
public class IntArraySignature {
    private final int[] array;

    /**
     * 函数功能：创建整数数组签名对象。
     * 输入：
     * - array：用于计算签名的整数数组。
     * 输出：无。
     */
    public IntArraySignature(int[] array) {
        this.array = array;
    }

    /**
     * 函数功能：计算整数数组签名的哈希值。
     * 输入：
     * - 无。
     * 输出：签名哈希值整数。
     */
    public int hashCode() {
        int hashCode = 1;
        for (int e : array) {
            hashCode = 31 * hashCode + e;
        }
        return hashCode;
    }

    /**
     * 函数功能：判断对象是否与当前整数数组签名相等。
     * 输入：
     * - o：待比较的对象。
     * 输出：是否相等的布尔值。
     */
    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof IntArraySignature && Arrays.equals(array, ((IntArraySignature) o).array);
    }
}
