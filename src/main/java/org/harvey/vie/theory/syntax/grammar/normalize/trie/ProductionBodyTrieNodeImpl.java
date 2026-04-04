package org.harvey.vie.theory.syntax.grammar.normalize.trie;

import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 19:57
 */
@Getter
public class ProductionBodyTrieNodeImpl implements ProductionBodyTrieNode {
    private Map<GrammarUnitSymbol, ProductionBodyTrieNode> children;
    private final GrammarUnitSymbol value;
    private boolean accept;

    public ProductionBodyTrieNodeImpl(GrammarUnitSymbol value) {
        this.value = value;
    }


    @Override
    public boolean accept() {
        return accept;
    }

    @Override
    public void markAccept() {
        accept = true;
    }

    @Override
    public ProductionBodyTrieNode getChild(GrammarUnitSymbol symbol) {
        return children == null ? null : children.get(symbol);
    }

    @Override
    public Iterator<ProductionBodyTrieNode> childrenIterator() {
        return children == null ? Collections.emptyIterator() : children.values().iterator();
    }

    @Override
    public boolean containsChild(GrammarUnitSymbol symbol) {
        return children != null && children.containsKey(symbol);
    }

    @Override
    public ProductionBodyTrieNode addChild(GrammarUnitSymbol symbol) {
        if (children == null) {
            children = new HashMap<>();
        }
        return children.computeIfAbsent(symbol, ProductionBodyTrieNodeImpl::new);
    }

    @Override
    public boolean childrenEmpty() {
        return children == null || children.isEmpty();
    }

    @Override
    public int childrenSize() {
        return children == null ? 0 : children.size();
    }

    @Override
    public String toString() {
        return value + "(" + (accept ? "*" : "") + ")";
    }
}
