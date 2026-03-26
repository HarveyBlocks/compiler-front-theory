package org.harvey.vie.theory.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class providing static methods and implementations for loading data
 * from an {@link InputStream}. It includes specialized loaders for common types
 * like integers and arrays.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 14:02
 */
public class Loaders {
    public static int loadInteger(InputStream is) throws IOException {
        return FromBytes.toInt(is.readNBytes(4));
    }

    public static int loadCharacter(InputStream is) throws IOException {
        return is.read();
    }

    public static ILoader<int[]> intArrayLoader() {
        return IntArrayLoader.INSTANCE;
    }

    public static byte[] loadBytes(InputStream is, int len) throws IOException {
        return is.readNBytes(len);
    }

    private static class IntArrayLoader implements ILoader<int[]> {
        private static final IntArrayLoader INSTANCE = new IntArrayLoader();

        private IntArrayLoader() {}

        @Override
        public int[] load(InputStream is) throws IOException {
            int len = loadInteger(is);
            int[] result = new int[len];
            for (int i = 0; i < len; i++) {
                result[i] = loadInteger(is);
            }
            return result;
        }
    }
}
