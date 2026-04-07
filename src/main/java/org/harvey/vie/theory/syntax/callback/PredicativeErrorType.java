package org.harvey.vie.theory.syntax.callback;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 15:31
 */
public enum PredicativeErrorType {
    /**
     * table element (production) is not found
     */
    UNDEFINED_PRODUCTION,
    /**
     * status stack is empty now
     */
    STACK_UNDERFLOW,
    /**
     * accepted but more token
     */
    TRAILING_INPUT_AFTER_ACCEPT,
    /**
     * accepted but stack not end able
     */
    INVALID_ACCEPTING_STATE,

    TERMINAL_CONFLICT,
}
