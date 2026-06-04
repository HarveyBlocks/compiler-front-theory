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
     * 函数功能：获取节点保存的语法符号。
     * 输入：
     * - 无。
     * 输出：GrammarUnitSymbol 类型返回值。
     */
    GrammarUnitSymbol getValue();

    /**
     * 函数功能：判断或获取接受状态。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean accept();

    /**
     * 函数功能：标记当前节点为接受节点。
     * 输入：
     * - 无。
     * 输出：无。
     */

    void markAccept();

    /**
     * 函数功能：获取指定语法符号对应的子节点。
     * 输入：
     * - symbol：GrammarUnitSymbol 类型参数。
     * 输出：ProductionBodyTrieNode 类型返回值。
     */

    ProductionBodyTrieNode getChild(GrammarUnitSymbol symbol);

    /**
     * 函数功能：获取子节点迭代器。
     * 输入：
     * - 无。
     * 输出：Iterator<ProductionBodyTrieNode> 类型集合或迭代结果。
     */

    Iterator<ProductionBodyTrieNode> childrenIterator();

    /**
     * 函数功能：判断是否包含指定子节点。
     * 输入：
     * - symbol：GrammarUnitSymbol 类型参数。
     * 输出：判断结果布尔值。
     */

    boolean containsChild(GrammarUnitSymbol symbol);

    /**
     * 函数功能：添加子节点并返回该节点。
     * 输入：
     * - symbol：GrammarUnitSymbol 类型参数。
     * 输出：ProductionBodyTrieNode 类型返回值。
     */

    ProductionBodyTrieNode addChild(GrammarUnitSymbol symbol);

    /**
     * 函数功能：判断子节点是否为空。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean childrenEmpty();

    /**
     * 函数功能：获取子节点数量。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    int childrenSize();

}
