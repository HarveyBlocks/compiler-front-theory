package org.harvey.vie.theory.lexical.analysis.token;

/**
 * Interface representing a lexical token extracted from the source code.
 * A token is the smallest meaningful unit for the subsequent parsing phase.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:13
 */
public interface SourceToken {
    String hintString();

    int getOffset();
}
