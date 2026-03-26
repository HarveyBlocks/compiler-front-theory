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

    public IntArraySignature(int[] array) {
        this.array = array;
    }

    public int hashCode() {
        int hashCode = 1;
        for (int e : array) {
            hashCode = 31 * hashCode + e;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof IntArraySignature && Arrays.equals(array, ((IntArraySignature) o).array);
    }
}
