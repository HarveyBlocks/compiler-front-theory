package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.util.SimpleCollection;

/**
 * TODO 产生式中的选择
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:49
 */
public interface GrammarAlternation extends GrammarSymbol, SimpleCollection<AlterableSymbol> {
    /**
     * 函数功能：添加候选语法符号。
     * 输入：
     * - symbol：AlterableSymbol 类型参数。
     * 输出：无。
     */
    void alternate(AlterableSymbol symbol);

    /**
     * 函数功能：设置指定位置的元素。
     * 输入：
     * - i：int 类型参数。
     * - concatenation：GrammarConcatenation 类型参数。
     * 输出：无。
     */

    void set(int i, GrammarConcatenation concatenation);

    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - i：int 类型参数。
     * 输出：AlterableSymbol 类型返回值。
     */

    AlterableSymbol get(int i);

    /**
     * 函数功能：添加空串候选产生式。
     * 输入：
     * - 无。
     * 输出：无。
     */

    void alternateEpsilon();

    /**
     * 函数功能：添加空串候选式。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean alternatedEpsilon();

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
        return false;
    }

}
