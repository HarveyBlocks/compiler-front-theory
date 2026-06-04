package org.harvey.vie.theory.io;

import java.nio.ByteBuffer;

/**
 * Utility class for converting high-level data types into their byte array
 * representations, facilitating serialization.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 14:44
 */
public class ToBytes {

    /**
     * 函数功能：将整数转换为字节数组。
     * 输入：
     * - value：待转换的整数值。
     * 输出：整数对应的 byte 数组。
     */
    public static byte[] fromInt(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }
}
