package org.harvey.vie.theory.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

/**
 * Utility class for persisting data structures to an {@link OutputStream}.
 * It provides methods for storing primitives and specialized {@link Storage}
 * implementations for arrays and other complex types.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 13:59
 */
public class Storages {
    /**
     * 函数功能：将整数写入输出流。
     * 输入：
     * - os：接收整数二进制数据的输出流。
     * - value：待写入的整数值。
     * 输出：写入的字节数。
     */
    public static int storeInteger(OutputStream os, int value) throws IOException {
        byte[] bytes = ToBytes.fromInt(value);
        os.write(bytes);
        return bytes.length;
    }

    /**
     * 函数功能：将字节数组写入输出流。
     * 输入：
     * - os：接收字节数据的输出流。
     * - data：待写入的字节数组。
     * 输出：写入的字节数。
     */
    public static int store(OutputStream os, byte[] data) throws IOException {
        os.write(data);
        return data.length;
    }

    /**
     * 函数功能：将位集合写入输出流。
     * 输入：
     * - os：接收位集合序列化数据的输出流。
     * - bitSet：待写入的位集合。
     * 输出：写入的字节数。
     */
    public static int storeBitSet(OutputStream os, BitSet bitSet) throws IOException {
        byte[] data = bitSet.toByteArray();
        int len = storeInteger(os, data.length);
        return len + store(os, data);
    }

    public static class IntArray implements Storage {
        private final int[] array;

        /**
         * 函数功能：创建整数数组存储对象。
         * 输入：
         * - array：待序列化的整数数组。
         * 输出：无。
         */
        public IntArray(int[] array) {this.array = array;}

        /**
         * 函数功能：将整数数组写入输出流。
         * 输入：
         * - os：接收整数数组序列化数据的输出流。
         * 输出：写入的字节数。
         */
        @Override
        public int store(OutputStream os) throws IOException {
            int cnt = storeInteger(os, array.length);
            for (int i : array) {
                cnt += storeInteger(os, i);
            }
            return cnt;
        }
    }
}
