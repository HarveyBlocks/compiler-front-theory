package org.harvey.vie.theory.exception;

/**
 * UncompletedOperation的异常
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 22:06
 */
public class UncompletedOperationException extends CompilerException {
    /**
     * 函数功能：创建无详细信息的未完成操作异常。
     * 输入：
     * - 无。
     * 输出：无。
     */
    public UncompletedOperationException() {
        super();
    }

    /**
     * 函数功能：创建带详细信息的未完成操作异常。
     * 输入：
     * - message：异常详细信息。
     * 输出：无。
     */
    public UncompletedOperationException(String message) {
        super(message);
    }

    /**
     * 函数功能：创建带详细信息和原因的未完成操作异常。
     * 输入：
     * - message：异常详细信息。
     * - cause：导致该异常的原因。
     * 输出：无。
     */
    public UncompletedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 函数功能：创建带原因的未完成操作异常。
     * 输入：
     * - cause：导致该异常的原因。
     * 输出：无。
     */
    public UncompletedOperationException(Throwable cause) {
        super(cause);
    }

    /**
     * 函数功能：创建可配置抑制和栈追踪行为的未完成操作异常。
     * 输入：
     * - message：异常详细信息。
     * - cause：导致该异常的原因。
     * - enableSuppression：是否启用异常抑制。
     * - writableStackTrace：是否生成可写栈追踪。
     * 输出：无。
     */
    protected UncompletedOperationException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
