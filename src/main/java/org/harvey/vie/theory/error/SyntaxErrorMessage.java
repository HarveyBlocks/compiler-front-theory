package org.harvey.vie.theory.error;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:24
 */
public class SyntaxErrorMessage extends AbstractErrorMessage {
    public SyntaxErrorMessage(int offset, String message) {
        super(offset, message);
    }

}
