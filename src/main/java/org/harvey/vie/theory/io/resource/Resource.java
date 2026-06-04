package org.harvey.vie.theory.io.resource;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.source.reader.SourceReader;

/**
 * Interface representing an input resource for the compiler. A resource
 * provides a way to obtain a {@link SourceReader} for processing its content.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 20:32
 */
public interface Resource {
    /**
     * 函数功能：将资源转换为源读取器。
     * 输入：
     * - errorContext：用于收集读取错误的错误上下文。
     * 输出：可读取资源内容的 SourceReader。
     */
    SourceReader toReader(ErrorContext errorContext);
}
