package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.io.ILoader;
import org.harvey.vie.theory.io.Storage;

/**
 * TODO 左部/Non-terminal
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:39
 */
public interface HeadDefineSymbol extends HeadSymbol, Storage {
    // 直接比较name不是很好, HeadDefineSymbol其实应该自己能够比较, 而不是必须要getName

    /**
     * 函数功能：获取名称。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */
    String getName();

    /**
     * 函数功能：判断当前头符号是否为定义符号。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    default boolean isDefine() {
        return true;
    }

    /**
     * 函数功能：转换为定义头符号。
     * 输入：
     * - 无。
     * 输出：HeadDefineSymbol 类型返回值。
     */

    @Override
    default HeadDefineSymbol toDefine() {
        return this;
    }

    interface Loader<T extends HeadDefineSymbol> extends ILoader<T> {
    }
}
