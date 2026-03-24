package org.harvey.vie.theory.lexical.analysis;

import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.StreamSourceTokenIterator;

/**
 * TODO Analyzer
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:11
 */
public interface LexicalAnalyzer {
    SourceTokenIterator iterator(String input);
    StreamSourceTokenIterator streamIterator();
}
