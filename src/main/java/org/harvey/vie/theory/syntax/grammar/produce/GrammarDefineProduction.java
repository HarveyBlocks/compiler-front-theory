package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:30
 */
public interface GrammarDefineProduction extends GrammarProduction {
    /**
     * 函数功能：获取非终结符定义。
     * 输入：
     * - 无。
     * 输出：HeadDefineSymbol 类型返回值。
     */
    HeadDefineSymbol getDefine();

    /**
     * 函数功能：获取产生式头部符号。
     * 输入：
     * - 无。
     * 输出：HeadSymbol 类型返回值。
     */

    @Override
    default HeadSymbol getHead() {
        return getDefine();
    }
}
