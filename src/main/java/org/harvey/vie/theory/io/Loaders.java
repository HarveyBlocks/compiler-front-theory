package org.harvey.vie.theory.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

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
    /**
     * 函数功能：从输入流读取整数。
     * 输入：
     * - is：提供整数二进制数据的输入流。
     * 输出：读取得到的 int 值。
     */
    public static int loadInteger(InputStream is) throws IOException {
        return FromBytes.toInt(is.readNBytes(4));
    }

    /**
     * 函数功能：从输入流读取一个字符值。
     * 输入：
     * - is：提供字符数据的输入流。
     * 输出：读取得到的字符整数值。
     */
    public static int loadCharacter(InputStream is) throws IOException {
        return is.read();
    }

    /**
     * 函数功能：获取整数数组加载器。
     * 输入：
     * - 无。
     * 输出：用于加载 int 数组的 ILoader。
     */
    public static ILoader<int[]> intArrayLoader() {
        return IntArrayLoader.INSTANCE;
    }

    /**
     * 函数功能：从输入流读取指定长度的字节数组。
     * 输入：
     * - is：提供字节数据的输入流。
     * - len：需要读取的字节长度。
     * 输出：读取得到的 byte 数组。
     */
    public static byte[] loadBytes(InputStream is, int len) throws IOException {
        return is.readNBytes(len);
    }

    /**
     * 函数功能：从输入流读取位集合。
     * 输入：
     * - is：提供位集合序列化数据的输入流。
     * 输出：读取得到的 BitSet。
     */
    public static BitSet loadBitSet(InputStream is) throws IOException {
        int len = Loaders.loadInteger(is);
        byte[] data = Loaders.loadBytes(is, len);
        return BitSet.valueOf(data);
    }

    private static class IntArrayLoader implements ILoader<int[]> {
        private static final IntArrayLoader INSTANCE = new IntArrayLoader();

        /**
         * 函数功能：创建整数数组加载器实例。
         * 输入：
         * - 无。
         * 输出：无。
         */
        private IntArrayLoader() {}

        /**
         * 函数功能：从输入流读取整数数组。
         * 输入：
         * - is：提供整数数组序列化数据的输入流。
         * 输出：读取得到的 int 数组。
         */
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
