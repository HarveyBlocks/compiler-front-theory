package org.harvey.vie.theory.syntax.grammar.normalize.trie;

import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 19:57
 */
@Getter
public class ProductionBodyTrieImpl implements ProductionBodyTrie {
    private final ProductionBodyTrieNode root;

    public ProductionBodyTrieImpl() {
        this.root = new ProductionBodyTrieNodeImpl(null);
    }

    @Override
    public void add(GrammarConcatenation concatenation) {
        ProductionBodyTrieNode cur = root;
        for (GrammarUnitSymbol symbol : concatenation) {
            cur = cur.addChild(symbol);
        }
        cur.markAccept();
    }

}
