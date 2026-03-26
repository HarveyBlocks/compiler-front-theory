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

    public static int toInt(byte[] value) {
        return ByteBuffer.wrap(value).getInt();
    }

    public static int toInt(byte[] bytes, int off) {
        return ByteBuffer.wrap(bytes, off, 4).getInt();
    }

    public static IntStream toIntArray(byte[] bytes) {
        return IntStream.range(0, bytes.length >> 2).map(off -> toInt(bytes, off << 2));
    }
}
