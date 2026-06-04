package org.harvey.vie.theory.semantic.tag;

import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 19:59
 */
public final class TagReducePredicateFactory {
    /**
     * 函数功能：创建 TagReducePredicateFactory 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private TagReducePredicateFactory() {
    }

    /**
     * 函数功能：创建语法标签判定器。
     * 输入：
     * - expected：SemanticTag... 类型参数。
     * 输出：ReducePredicate 类型返回值。
     */

    public static ReducePredicate predicate(SemanticTag... expected) {
        return production -> production.matchTags(expected);
    }
}
