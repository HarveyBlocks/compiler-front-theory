package org.harvey.vie.theory.io.resource;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.source.reader.AsciiStringSourceReader;
import org.harvey.vie.theory.source.reader.SourceReader;

import java.io.StringReader;

/**
 * AN implementation of {@link Resource} that uses an ASCII string as its source.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 20:59
 */
@AllArgsConstructor
public class AsciiStringResource implements Resource {
    private final String s;

    /**
     * 函数功能：将 ASCII 字符串资源转换为源读取器。
     * 输入：
     * - errorContext：用于收集读取错误的错误上下文。
     * 输出：可读取该资源内容的 SourceReader。
     */
    @Override
    public SourceReader toReader(ErrorContext errorContext) {
        return new AsciiStringSourceReader(new StringReader(s), errorContext);
    }
}
