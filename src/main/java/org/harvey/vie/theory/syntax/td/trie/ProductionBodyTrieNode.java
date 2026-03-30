package org.harvey.vie.theory.syntax.td.trie;

import org.harvey.vie.theory.syntax.grammar.symbol.ConcatenableSymbol;

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
    ConcatenableSymbol getValue();

    boolean accept();

    void markAccept();

    ProductionBodyTrieNode getChild(ConcatenableSymbol symbol);

    /**
     * @return null for children is empty
     */
    Iterator<ProductionBodyTrieNode> childrenIterator();

    boolean containsChild(ConcatenableSymbol symbol);

    ProductionBodyTrieNode addChild(ConcatenableSymbol symbol);

    boolean childrenEmpty();

    int childrenSize();

}
