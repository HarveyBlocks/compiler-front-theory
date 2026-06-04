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
    /**
     * 函数功能：添加指定元素。
     * 输入：
     * - concatenation：GrammarConcatenation 类型参数。
     * 输出：无。
     */
    void add(GrammarConcatenation concatenation);

    /**
     * 函数功能：获取根节点。
     * 输入：
     * - 无。
     * 输出：ProductionBodyTrieNode 类型返回值。
     */

    ProductionBodyTrieNode getRoot();

}
