package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.io.ILoader;
import org.harvey.vie.theory.io.Storage;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

/**
 * TODO 没有 `|` 的产生式
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 00:59
 */
public interface SimpleGrammarProduction extends Storage {
    /**
     * 函数功能：获取产生式头部符号。
     * 输入：
     * - 无。
     * 输出：HeadSymbol 类型返回值。
     */
    HeadSymbol getHead();
/**
 * 函数功能：获取产生式体。
 * 输入：
 * - 无。
 * 输出：AlterableSymbol 类型返回值。
 */

    AlterableSymbol getBody();
/**
 * 函数功能：获取语义标签数组。
 * 输入：
 * - 无。
 * 输出：SemanticTag[] 类型数组。
 */

    SemanticTag[] getTags();
/**
 * 函数功能：判断是否包含指定语义标签。
 * 输入：
 * - tag：SemanticTag 类型参数。
 * 输出：判断结果布尔值。
 */

    boolean containsTag(SemanticTag tag);
/**
 * 函数功能：判断语义标签是否匹配。
 * 输入：
 * - expected：SemanticTag... 类型参数。
 * 输出：判断结果布尔值。
 */

    boolean matchTags(SemanticTag... expected);

    interface Loader<T extends SimpleGrammarProduction> extends ILoader<T> {
    }
}
