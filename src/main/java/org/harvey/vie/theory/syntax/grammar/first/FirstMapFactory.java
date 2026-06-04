package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO First(X)
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:36
 */
public interface FirstMapFactory {
    /**
     * 函数功能：获取 FIRST 集合。
     * 输入：
     * - context：ProductionSetContext 类型参数。
     * 输出：FirstMap 类型返回值。
     */
    FirstMap first(ProductionSetContext context);
}
