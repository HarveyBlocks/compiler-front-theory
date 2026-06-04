package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

import java.util.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 16:40
 */
public abstract class AbstractTagGrammarSymbol implements TagGrammarSymbol {
    private final Set<SemanticTag> tags;
/**
 * 函数功能：创建 AbstractTagGrammarSymbol 对象。
 * 输入：
 * - tags：Set<SemanticTag> 类型参数。
 * 输出：无。
 */

    protected AbstractTagGrammarSymbol(Set<SemanticTag> tags) {this.tags = tags;}
/**
 * 函数功能：创建 AbstractTagGrammarSymbol 对象。
 * 输入：
 * - 无。
 * 输出：无。
 */

    protected AbstractTagGrammarSymbol() {this(new HashSet<>());}
/**
 * 函数功能：添加语义标签。
 * 输入：
 * - array：SemanticTag[] 类型参数。
 * 输出：无。
 */

    @Override
    public void addTag(SemanticTag[] array) {
        tags.addAll(Arrays.asList(array));
    }
/**
 * 函数功能：获取语义标签数组。
 * 输入：
 * - 无。
 * 输出：SemanticTag[] 类型数组。
 */

    @Override
    public SemanticTag[] getTags() {
        return tags.toArray(SemanticTag[]::new);
    }
}
