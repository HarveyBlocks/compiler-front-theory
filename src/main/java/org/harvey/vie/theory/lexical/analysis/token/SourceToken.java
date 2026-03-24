package org.harvey.vie.theory.lexical.analysis.token;

/**
 * TODO 词元
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:13
 */
public interface SourceToken {
    String hintString();

    int getColumn();
    int getLine();
}
