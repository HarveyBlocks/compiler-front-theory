package org.harvey.vie.theory.source.reader;

import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.source.character.SourceCharacter;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface for reading characters from a source input stream.
 * It extends {@link Closeable} and provides methods to read characters
 * sequentially or peek at the next character without advancing the pointer.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:44
 */
public interface SourceReader extends Closeable {

    /**
     * 函数功能：读取一个源字符并移动读取位置。
     * 输入：
     * - 无。
     * 输出：读取到的源字符。
     */
    SourceCharacter read() throws IOException, CompileException;

    /**
     * 函数功能：读取一个源字符但不移动读取位置。
     * 输入：
     * - 无。
     * 输出：预览到的源字符。
     */
    SourceCharacter peek() throws IOException, CompileException;

    /**
     * 函数功能：关闭源读取器。
     * 输入：
     * - 无。
     * 输出：无。
     */
    void close();

    /**
     * 函数功能：获取当前读取偏移量。
     * 输入：
     * - 无。
     * 输出：当前读取偏移量。
     */
    int getOffset();
}
