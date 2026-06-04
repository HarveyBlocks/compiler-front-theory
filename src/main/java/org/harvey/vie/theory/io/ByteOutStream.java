package org.harvey.vie.theory.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * A utility class for building byte arrays. It provides methods to write
 * byte sequences and to "flatten" multiple byte arrays into a single contiguous array.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 16:43
 */
public class ByteOutStream {
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    /**
     * 函数功能：将多个字节数组拼接为一个连续字节数组。
     * 输入：
     * - data：待拼接的二维字节数组。
     * 输出：拼接后的 byte 数组。
     */
    public static byte[] flap(byte[][] data) {
        ByteOutStream os = new ByteOutStream();
        Arrays.stream(data).forEach(e -> os.write(e, 0, e.length));
        return os.toByteArray();
    }

    /**
     * 函数功能：获取当前输出流中的全部字节数据。
     * 输入：
     * - 无。
     * 输出：当前内容对应的 byte 数组。
     */
    public byte[] toByteArray() {
        return os.toByteArray();
    }

    /**
     * 函数功能：写入字节数组的指定片段。
     * 输入：
     * - row：待写入的字节数组。
     * - off：写入起始偏移量。
     * - len：写入字节长度。
     * 输出：无。
     */
    public void write(byte[] row, int off, int len) {
        os.write(row, off, len);
    }

    /**
     * 函数功能：写入完整字节数组。
     * 输入：
     * - row：待写入的字节数组。
     * 输出：无。
     */
    public void write(byte[] row) throws IOException {
        os.write(row);
    }

    /**
     * 函数功能：获取当前输出流内容的字符串表示。
     * 输入：
     * - 无。
     * 输出：当前内容对应的字符串。
     */
    @Override
    public String toString() {
        return os.toString();
    }
}
