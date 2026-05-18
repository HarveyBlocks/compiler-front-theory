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

    public static byte[] fromInt(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }
}
