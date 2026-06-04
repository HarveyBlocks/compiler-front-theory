package org.harvey.vie.theory.syntax.grammar.normalize.trie;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 20:46
 */
public interface ProductionBodyTrieFactory {
    /**
     * 函数功能：创建目标对象。
     * 输入：
     * - body：GrammarAlternation 类型参数。
     * 输出：ProductionBodyTrie 类型返回值。
     */
    ProductionBodyTrie create(GrammarAlternation body);
}
