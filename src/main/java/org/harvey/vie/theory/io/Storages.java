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
    public static int storeInteger(OutputStream os, int value) throws IOException {
        byte[] bytes = ToBytes.fromInt(value);
        os.write(bytes);
        return bytes.length;
    }

    public static int store(OutputStream os, byte[] data) throws IOException {
        os.write(data);
        return data.length;
    }

    public static int storeBitSet(OutputStream os, BitSet bitSet) throws IOException {
        byte[] data = bitSet.toByteArray();
        int len = storeInteger(os, data.length);
        return len + store(os, data);
    }

    public static class IntArray implements Storage {
        private final int[] array;

        public IntArray(int[] array) {this.array = array;}

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
