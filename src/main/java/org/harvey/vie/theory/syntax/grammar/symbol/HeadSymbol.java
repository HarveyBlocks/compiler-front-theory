package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 01:19
 */
public interface HeadSymbol extends TagGrammarSymbol, GrammarUnitSymbol {
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
 * 函数功能：转换为非终结符。
 * 输入：
 * - 无。
 * 输出：HeadSymbol 类型返回值。
 */

    @Override
    default HeadSymbol toHead() {
        return this;
    }
/**
 * 函数功能：判断当前头符号是否为定义符号。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    default boolean isDefine() {
        throw GrammarSymbol.unsupportedTest();
    }
/**
 * 函数功能：转换为定义头符号。
 * 输入：
 * - 无。
 * 输出：HeadDefineSymbol 类型返回值。
 */

    default HeadDefineSymbol toDefine() {
        throw GrammarSymbol.unsupportedCast();
    }
}
