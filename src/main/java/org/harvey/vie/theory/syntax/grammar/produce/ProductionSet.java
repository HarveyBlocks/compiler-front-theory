package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.ReferredHeadSymbol;
import org.harvey.vie.theory.util.SimpleCollection;

/**
 * TODO 产生式集合, 也就是文法. 已经被映射了
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:48
 */
public interface ProductionSet extends SimpleCollection<GrammarProduction> {
    /**
     * 函数功能：获取产生式头部符号。
     * 输入：
     * - i：int 类型参数。
     * 输出：ReferredHeadSymbol 类型返回值。
     */
    ReferredHeadSymbol getHead(int i);

    /**
     * 函数功能：获取产生式体。
     * 输入：
     * - i：int 类型参数。
     * 输出：AlterableSymbol 类型返回值。
     */

    AlterableSymbol getBody(int i);

    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - i：int 类型参数。
     * 输出：GrammarProduction 类型返回值。
     */

    GrammarProduction get(int i);

}
