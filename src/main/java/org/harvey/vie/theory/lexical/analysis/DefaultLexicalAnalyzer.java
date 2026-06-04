package org.harvey.vie.theory.lexical.analysis;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.io.resource.Resource;
import org.harvey.vie.theory.lexical.alphabet.SourceAlphabetCharacterAdaptor;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.lexical.analysis.token.StatusTableTokenIterator;
import org.harvey.vie.theory.lexical.dfa.status.RegexDfaStatusTable;

/**
 * Default implementation of the {@link LexicalAnalyzer} interface.
 * It serves as a factory for creating {@link StatusTableTokenIterator} instances,
 * providing them with the necessary transition first and character adaptor.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:23
 */
@AllArgsConstructor
public class DefaultLexicalAnalyzer implements LexicalAnalyzer {
    private final RegexDfaStatusTable table;
    private final SourceAlphabetCharacterAdaptor saca;

    /**
     * 函数功能：创建源词法单元迭代器。
     * 输入：
     * - errorContext：用于收集词法错误的错误上下文。
     * - resource：待分析的输入资源。
     * 输出：用于读取源词法单元的 SourceTokenIterator。
     */
    @Override
    public SourceTokenIterator iterator(ErrorContext errorContext, Resource resource) {
        return new StatusTableTokenIterator(errorContext, resource.toReader(errorContext), saca, table);
    }
}
