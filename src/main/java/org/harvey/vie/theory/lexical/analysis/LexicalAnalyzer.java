package org.harvey.vie.theory.lexical.analysis;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.io.resource.Resource;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;

/**
 * Interface for a lexical analyzer (scanner). A lexical analyzer is responsible
 * for breaking down the input source into a stream of tokens. Each analyzer
 * instance is typically associated with a specific state machine configuration.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:11
 */
public interface LexicalAnalyzer {
    SourceTokenIterator iterator(ErrorContext errorContext, Resource resource);
}
