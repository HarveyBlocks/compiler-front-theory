package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 01:00
 */
public interface ConcatenableSymbol extends GrammarSymbol {
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
 * 函数功能：判断当前符号是否可连接。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    default boolean isConcatenable() {
        return true;
    }
/**
 * 函数功能：转换为可连接语法符号。
 * 输入：
 * - 无。
 * 输出：ConcatenableSymbol 类型返回值。
 */

    @Override
    default ConcatenableSymbol toConcatenable() {
        return this;
    }
}

