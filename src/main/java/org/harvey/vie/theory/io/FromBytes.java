package org.harvey.vie.theory.io;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

/**
 * Utility class for converting byte arrays back into higher-level data structures.
 * This class provides static methods to deserialize primitive types and streams from
 * byte representations, facilitating data recovery from storage or network streams.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 14:42
 */
public class FromBytes {

    /**
     * 函数功能：将字节数组转换为整数。
     * 输入：
     * - value：包含整数二进制表示的字节数组。
     * 输出：转换得到的 int 值。
     */
    public static int toInt(byte[] value) {
        return ByteBuffer.wrap(value).getInt();
    }

    /**
     * 函数功能：从字节数组指定偏移处转换整数。
     * 输入：
     * - bytes：包含整数二进制表示的字节数组。
     * - off：读取整数的起始偏移量。
     * 输出：转换得到的 int 值。
     */
    public static int toInt(byte[] bytes, int off) {
        return ByteBuffer.wrap(bytes, off, 4).getInt();
    }

    /**
     * 函数功能：将字节数组按整数宽度转换为整数流。
     * 输入：
     * - bytes：包含连续整数二进制表示的字节数组。
     * 输出：转换得到的 IntStream。
     */
    public static IntStream toIntArray(byte[] bytes) {
        return IntStream.range(0, bytes.length >> 2).map(off -> toInt(bytes, off << 2));
    }
}
