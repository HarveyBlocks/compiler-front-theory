package org.harvey.vie.theory.syntax.grammar.normalize;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;

/**
 * TODO 消除最左因子
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 16:32
 */
public interface LeftRecursionEliminator {
    /**
     * 函数功能：消除语法中的指定结构。
     * 输入：
     * - context：ProductionSetContext 类型参数。
     * 输出：ProductionSetContext 类型返回值。
     */
    ProductionSetContext eliminate(ProductionSetContext context);

    /**
     * 让用户自己构造这个名字的构造器是因为, 如果在内部构造而对用户透明的话, 用户可能会产生错误的文法.
     * 比如本系统直接生成 A' 来消解 A 的左递归, 但如果用户不知情, 已经使用了 A' 作为定义非终结符. 那么将导致非终结符的名字重叠.
     * 因此让代码的调用者来决定实现.
     */
    @FunctionalInterface
    interface DefineNameFactory {
        /**
         * 函数功能：创建目标对象。
         * 输入：
         * - origin：String 类型参数。
         * 输出：字符串结果。
         */
        String create(String origin);
    }

}
