package org.harvey.vie.theory.syntax.td.trie;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 20:46
 */
public interface ProductionBodyTrieFactory {
    ProductionBodyTrie create(GrammarAlternation body);
}
