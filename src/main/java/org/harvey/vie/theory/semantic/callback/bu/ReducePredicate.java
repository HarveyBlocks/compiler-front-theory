package org.harvey.vie.theory.semantic.callback.bu;

import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 02:49
 */
@FunctionalInterface
public interface ReducePredicate {
    /**
     * 函数功能：判断输入是否满足条件。
     * 输入：
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：判断结果布尔值。
     */
    boolean test(SimpleGrammarProduction production);
}
