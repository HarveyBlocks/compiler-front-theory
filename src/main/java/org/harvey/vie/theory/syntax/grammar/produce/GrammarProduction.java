package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

/**
 * TODO 产生式
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:48
 */
public interface GrammarProduction {
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
 * 输出：GrammarAlternation 类型返回值。
 */

    GrammarAlternation getBody();
}
