package org.harvey.vie.theory.syntax.grammar.normalize.trie;

import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 20:46
 */
public class ProductionBodyTrieFactoryImpl implements ProductionBodyTrieFactory {
    @Override
    public ProductionBodyTrie create(GrammarAlternation body) {
        ProductionBodyTrieImpl trie = new ProductionBodyTrieImpl();
        for (AlterableSymbol symbol : body) {
            if (symbol.isEpsilon() || !symbol.isConcatenable()) {
                continue;
            }
            if (symbol.isConcatenation()) {
                trie.add(symbol.toConcatenation());
            }
        }
        return trie;
    }
}
