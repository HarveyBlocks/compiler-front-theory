package org.harvey.vie.theory.source.reader;

import lombok.Getter;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.IoErrorMessage;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.source.character.AsciiCharacter;
import org.harvey.vie.theory.source.character.SourceCharacter;

import java.io.IOException;
import java.io.StringReader;

/**
 * AN implementation of {@link SourceReader} that reads characters from a string.
 * This reader is restricted to ASCII characters and will report an error if
 * a non-ASCII character is encountered in the source.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:56
 */
public class AsciiStringSourceReader implements SourceReader {
    private final StringReader reader;
    private final ErrorContext errorContext;
    @Getter
    private int offset;

    /**
     * 函数功能：创建基于字符串读取器的 ASCII 源读取器。
     * 输入：
     * - reader：提供源字符的字符串读取器。
     * - errorContext：用于记录读取错误的错误上下文。
     * 输出：无。
     */
    public AsciiStringSourceReader(StringReader reader, ErrorContext errorContext) {
        this.reader = reader;
        this.errorContext = errorContext;
        this.offset = 0;
    }

    /**
     * 函数功能：读取一个 ASCII 源字符并移动读取位置。
     * 输入：
     * - 无。
     * 输出：读取到的源字符；结束时返回 EOF。
     */
    @Override
    public SourceCharacter read() throws IOException, CompileException {
        offset++;
        int ch = reader.read();
        if (ch == -1) {
            return SourceCharacter.EOF;
        }
        // 仅支持 ascii
        if ((ch & 0xff_ff_ff_00) > 0) {
            errorContext.addError(new IoErrorMessage(offset, "ascii only"));
            throw new CompileException();
        }
        return new AsciiCharacter((byte) ch);
    }

    /**
     * 函数功能：预览一个 ASCII 源字符但不移动读取位置。
     * 输入：
     * - 无。
     * 输出：预览到的源字符；结束时返回 EOF。
     */
    @Override
    public SourceCharacter peek() throws IOException, CompileException {
        reader.mark(1);
        SourceCharacter ch = read();
        offset--;
        reader.reset();
        return ch;
    }

    /**
     * 函数功能：关闭底层字符串读取器。
     * 输入：
     * - 无。
     * 输出：无。
     */
    @Override
    public void close() {
        reader.close();
    }
}
