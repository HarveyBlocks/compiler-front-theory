package org.harvey.vie.theory.syntax;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;

/**
 * TODO 语法分析
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:16
 */
public interface SyntaxPhaser {
    void phase(SourceTokenIterator iterator, ErrorContext errorContext);
}
