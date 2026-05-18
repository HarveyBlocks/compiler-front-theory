package org.harvey.vie.theory.error;

/**
 * Represents an error message encountered during input/output operations
 * within the compilation process.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 01:00
 */
public class IoErrorMessage extends AbstractErrorMessage {

    public IoErrorMessage(int offset, String message) {
        super(offset, message);
    }
}
