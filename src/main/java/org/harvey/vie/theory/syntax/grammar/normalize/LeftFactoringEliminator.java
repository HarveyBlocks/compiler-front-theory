package org.harvey.vie.theory.syntax.grammar.normalize;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO 消除左公共因子
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 18:38
 */
public interface LeftFactoringEliminator {
    /**
     * 函数功能：消除语法中的指定结构。
     * 输入：
     * - context：ProductionSetContext 类型参数。
     * 输出：ProductionSetContext 类型返回值。
     */
    ProductionSetContext eliminate(ProductionSetContext context);

    /**
     * 使用这个类的理由同{@link LeftRecursionEliminator.DefineNameFactory}.
     * 但是由于消除左递归, 一个产生式只会产生一个中间产生式; 而消除左公共会产生多个, 因此稍作差别
     */
    @FunctionalInterface
    interface DefineNameFactory {
        /**
         * 函数功能：创建目标对象。
         * 输入：
         * - origin：String 类型参数。
         * - count：int 类型参数。
         * 输出：字符串结果。
         */
        String create(String origin, int count);
    }
}
