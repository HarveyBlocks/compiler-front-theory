package org.harvey.vie.theory.syntax.grammar;

import org.harvey.vie.theory.syntax.grammar.produce.GrammarProduction;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSet;

/**
 * TODO 构造文法
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:47
 */
public interface GrammarBuilder {
    /**
     * 函数功能：添加语法产生式。
     * 输入：
     * - production：GrammarProduction 类型参数。
     * 输出：GrammarBuilder 类型返回值。
     */
    GrammarBuilder addProduction(GrammarProduction production);

    /**
     * 函数功能：构建目标对象。
     * 输入：
     * - 无。
     * 输出：ProductionSet 类型返回值。
     */

    ProductionSet build();
}
