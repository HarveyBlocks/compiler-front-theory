package org.harvey.vie.theory.syntax.td.trie;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarSymbol;

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
        for (GrammarSymbol symbol : body) {
            if (symbol instanceof GrammarConcatenation) {
                trie.add((GrammarConcatenation) symbol);
            }
        }
        return trie;
    }
}
