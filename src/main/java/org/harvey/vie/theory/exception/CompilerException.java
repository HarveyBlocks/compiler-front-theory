package org.harvey.vie.theory.exception;

/**
 * Compiler的异常. 编译过程中发生的异常. (构建编译器的构建, 比如自动机的, 不算此类)
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 22:07
 */
public class CompilerException extends RuntimeException {
    /**
     * 函数功能：创建无详细信息的编译器运行时异常。
     * 输入：
     * - 无。
     * 输出：无。
     */
    public CompilerException() {
        super();
    }

    /**
     * 函数功能：创建带详细信息的编译器运行时异常。
     * 输入：
     * - message：异常详细信息。
     * 输出：无。
     */
    public CompilerException(String message) {
        super(message);
    }

    /**
     * 函数功能：创建带详细信息和原因的编译器运行时异常。
     * 输入：
     * - message：异常详细信息。
     * - cause：导致该异常的原因。
     * 输出：无。
     */
    public CompilerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 函数功能：创建带原因的编译器运行时异常。
     * 输入：
     * - cause：导致该异常的原因。
     * 输出：无。
     */
    public CompilerException(Throwable cause) {
        super(cause);
    }

    /**
     * 函数功能：创建可配置抑制和栈追踪行为的编译器运行时异常。
     * 输入：
     * - message：异常详细信息。
     * - cause：导致该异常的原因。
     * - enableSuppression：是否启用异常抑制。
     * - writableStackTrace：是否生成可写栈追踪。
     * 输出：无。
     */
    protected CompilerException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
