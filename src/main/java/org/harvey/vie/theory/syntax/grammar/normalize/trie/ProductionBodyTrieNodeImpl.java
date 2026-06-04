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
    private final GrammarUnitSymbol value;
    private Map<GrammarUnitSymbol, ProductionBodyTrieNode> children;
    private boolean accept;

    /**
     * 函数功能：创建 ProductionBodyTrieNodeImpl 对象。
     * 输入：
     * - value：GrammarUnitSymbol 类型参数。
     * 输出：无。
     */

    public ProductionBodyTrieNodeImpl(GrammarUnitSymbol value) {
        this.value = value;
    }

    /**
     * 函数功能：判断或获取接受状态。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */


    @Override
    public boolean accept() {
        return accept;
    }

    /**
     * 函数功能：标记当前节点为接受节点。
     * 输入：
     * - 无。
     * 输出：无。
     */

    @Override
    public void markAccept() {
        accept = true;
    }

    /**
     * 函数功能：获取指定语法符号对应的子节点。
     * 输入：
     * - symbol：GrammarUnitSymbol 类型参数。
     * 输出：ProductionBodyTrieNode 类型返回值。
     */

    @Override
    public ProductionBodyTrieNode getChild(GrammarUnitSymbol symbol) {
        return children == null ? null : children.get(symbol);
    }

    /**
     * 函数功能：获取子节点迭代器。
     * 输入：
     * - 无。
     * 输出：Iterator<ProductionBodyTrieNode> 类型集合或迭代结果。
     */

    @Override
    public Iterator<ProductionBodyTrieNode> childrenIterator() {
        return children == null ? Collections.emptyIterator() : children.values().iterator();
    }

    /**
     * 函数功能：判断是否包含指定子节点。
     * 输入：
     * - symbol：GrammarUnitSymbol 类型参数。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean containsChild(GrammarUnitSymbol symbol) {
        return children != null && children.containsKey(symbol);
    }

    /**
     * 函数功能：添加子节点并返回该节点。
     * 输入：
     * - symbol：GrammarUnitSymbol 类型参数。
     * 输出：ProductionBodyTrieNode 类型返回值。
     */

    @Override
    public ProductionBodyTrieNode addChild(GrammarUnitSymbol symbol) {
        if (children == null) {
            children = new HashMap<>();
        }
        return children.computeIfAbsent(symbol, ProductionBodyTrieNodeImpl::new);
    }

    /**
     * 函数功能：判断子节点是否为空。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean childrenEmpty() {
        return children == null || children.isEmpty();
    }

    /**
     * 函数功能：获取子节点数量。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    @Override
    public int childrenSize() {
        return children == null ? 0 : children.size();
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return value + "(" + (accept ? "*" : "") + ")";
    }
}
