package org.harvey.vie.theory.error;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:24
 */
public class SyntaxErrorMessage implements CompileErrorMessage {
    private final int offset;
    private final String message;

    public SyntaxErrorMessage(int offset, String message) {
        this.offset = offset;
        this.message = message;
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
