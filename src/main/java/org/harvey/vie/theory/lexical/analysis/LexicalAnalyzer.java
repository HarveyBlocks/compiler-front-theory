package org.harvey.vie.theory.lexical.analysis;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.resource.Resource;

/**
 * TODO Analyzer. 一个状态机对应一个LexicalAnalyzer
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:11
 */
public interface LexicalAnalyzer {
    SourceTokenIterator iterator(ErrorContext errorContext, Resource resource);
}
