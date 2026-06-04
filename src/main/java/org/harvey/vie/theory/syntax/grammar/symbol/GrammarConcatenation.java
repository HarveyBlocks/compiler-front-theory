package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.util.IRandomAccess;

/**
 * TODO 产生式中的连接
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:49
 */
public interface GrammarConcatenation extends ConcatenableSymbol, AlterableSymbol, IRandomAccess<GrammarUnitSymbol> {
/**
 * 函数功能：连接语法符号。
 * 输入：
 * - concatenable：ConcatenableSymbol 类型参数。
 * 输出：无。
 */

    void concatenate(ConcatenableSymbol concatenable);
/**
 * 函数功能：判断当前产生式项目是否为空串。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */


    @Override
    default boolean isEpsilon() {
        return false;
    }
/**
 * 函数功能：判断当前符号是否为终结符。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    default boolean isTerminal() {
        return false;
    }
/**
 * 函数功能：判断当前符号是否为连接体。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */


    @Override
    default boolean isConcatenation() {
        return true;
    }
/**
 * 函数功能：转换为语法符号连接体。
 * 输入：
 * - 无。
 * 输出：GrammarConcatenation 类型返回值。
 */

    @Override
    default GrammarConcatenation toConcatenation() {
        return this;
    }

}

