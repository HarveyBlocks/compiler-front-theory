package org.harvey.vie.theory.syntax.grammar.normalize.trie;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;

import java.util.Iterator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 18:41
 */
public interface ProductionBodyTrieNode {
    /**
     * @return null for root
     */
    GrammarUnitSymbol getValue();

    boolean accept();

    void markAccept();

    ProductionBodyTrieNode getChild(GrammarUnitSymbol symbol);

    /**
     * @return null for children is empty
     */
    Iterator<ProductionBodyTrieNode> childrenIterator();

    boolean containsChild(GrammarUnitSymbol symbol);

    ProductionBodyTrieNode addChild(GrammarUnitSymbol symbol);

    boolean childrenEmpty();

    int childrenSize();

}
