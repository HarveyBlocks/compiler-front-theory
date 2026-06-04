package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 23:53
 */
public interface GrammarUnitSymbol extends ConcatenableSymbol {
/**
 * 函数功能：判断当前符号是否为连接体。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    default boolean isConcatenation() {
        return false;
    }
/**
 * 函数功能：转换为语法单元符号。
 * 输入：
 * - 无。
 * 输出：GrammarUnitSymbol 类型返回值。
 */

    @Override
    default GrammarUnitSymbol toUnit() {
        return this;
    }
}
