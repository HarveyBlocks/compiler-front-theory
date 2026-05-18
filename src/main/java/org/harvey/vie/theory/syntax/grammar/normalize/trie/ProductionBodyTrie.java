package org.harvey.vie.theory.syntax.grammar.normalize.trie;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;

/**
 * TODO 前缀树, 不处理任何epsilon, 遇到epsilon直接无视
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 18:44
 */
public interface ProductionBodyTrie {
    void add(GrammarConcatenation concatenation);

    ProductionBodyTrieNode getRoot();

}
