package org.harvey.vie.theory.exception;

/**
 * UncompletedOperation的异常
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 22:06
 */
public class UncompletedOperationException extends CompilerException {
    public UncompletedOperationException() {
        super();
    }

    public UncompletedOperationException(String message) {
        super(message);
    }

    public UncompletedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncompletedOperationException(Throwable cause) {
        super(cause);
    }

    protected UncompletedOperationException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
