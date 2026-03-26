package org.harvey.vie.theory.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an error message specifically identified during the lexical
 * analysis phase of compilation.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:29
 */
@Getter
public class LexicalErrorMessage extends AbstractErrorMessage {
    public LexicalErrorMessage(int offset, String message) {
        super(offset, message);
    }
}
