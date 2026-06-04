package org.harvey.vie.theory.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Abstract base class for {@link CompileErrorMessage} implementations.
 * It provides common storage and string representation for error messages
 * occurring at specific offsets in the source code.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 01:00
 */
@AllArgsConstructor
@Getter
public abstract class AbstractErrorMessage implements CompileErrorMessage {
    private final int offset;
    private final String message;

    /**
     * 函数功能：返回错误偏移量和错误信息组成的字符串。
     * 输入：
     * - 无。
     * 输出：错误信息的字符串表示。
     */
    @Override
    public String toString() {
        return offset + ": " + message;
    }
}
