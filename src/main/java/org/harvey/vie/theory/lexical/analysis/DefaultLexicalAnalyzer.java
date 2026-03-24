package org.harvey.vie.theory.lexical.analysis;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.StatusTableTokenIterator;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;
import org.harvey.vie.theory.resource.Resource;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:23
 */
@AllArgsConstructor
public class DefaultLexicalAnalyzer implements LexicalAnalyzer {
    private final DfaStatusTable table;

    @Override
    public SourceTokenIterator iterator(ErrorContext errorContext, Resource resource) {
        return new StatusTableTokenIterator(errorContext, resource.toReader(errorContext), table);
    }
}
