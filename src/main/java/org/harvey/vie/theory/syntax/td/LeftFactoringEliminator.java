package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO 消除左公共因子
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 18:38
 */
public interface LeftFactoringEliminator {
    ProductionSetContext eliminate(ProductionSetContext context);

    /**
     * 使用这个类的理由同{@link LeftRecursionEliminator.DefineNameFactory}.
     * 但是由于消除左递归, 一个产生式只会产生一个中间产生式; 而消除左公共会产生多个, 因此稍作差别
     */
    @FunctionalInterface
    interface DefineNameFactory {
        String create(String origin, int count);
    }
}
