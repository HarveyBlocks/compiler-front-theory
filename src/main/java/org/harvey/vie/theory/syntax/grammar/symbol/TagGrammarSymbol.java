package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 16:44
 */
public interface TagGrammarSymbol {
    /**
     * 函数功能：添加语义标签。
     * 输入：
     * - array：SemanticTag[] 类型参数。
     * 输出：无。
     */
    void addTag(SemanticTag[] array);
/**
 * 函数功能：获取语义标签数组。
 * 输入：
 * - 无。
 * 输出：SemanticTag[] 类型数组。
 */

    SemanticTag[] getTags();
}
