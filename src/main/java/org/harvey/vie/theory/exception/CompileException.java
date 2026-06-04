package org.harvey.vie.theory.exception;

/**
 * 编译时发生的异常, 一般会被弥补的异常
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 01:08
 */
public class CompileException extends Exception {
    /**
     * 函数功能：创建无详细信息的编译异常。
     * 输入：
     * - 无。
     * 输出：无。
     */
    public CompileException() {
        super();
    }

    /**
     * 函数功能：创建带详细信息的编译异常。
     * 输入：
     * - message：异常详细信息。
     * 输出：无。
     */
    public CompileException(String message) {
        super(message);
    }

    /**
     * 函数功能：创建带详细信息和原因的编译异常。
     * 输入：
     * - message：异常详细信息。
     * - cause：导致该异常的原因。
     * 输出：无。
     */
    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 函数功能：创建带原因的编译异常。
     * 输入：
     * - cause：导致该异常的原因。
     * 输出：无。
     */
    public CompileException(Throwable cause) {
        super(cause);
    }

    /**
     * 函数功能：创建可配置抑制和栈追踪行为的编译异常。
     * 输入：
     * - message：异常详细信息。
     * - cause：导致该异常的原因。
     * - enableSuppression：是否启用异常抑制。
     * - writableStackTrace：是否生成可写栈追踪。
     * 输出：无。
     */
    protected CompileException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
