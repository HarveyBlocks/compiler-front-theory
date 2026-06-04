package org.harvey.vie.theory.semantic.command.translator.command;

/**
 * Strongly-typed operator descriptor used by command translators.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:14
 */
public interface OperatorFactor {
    /**
     * 函数功能：获取操作符助记符。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */
    String mnemonic();

    /**
     * 函数功能：获取操作符类别。
     * 输入：
     * - 无。
     * 输出：OperatorCategory 类型返回值。
     */

    OperatorCategory category();

    /**
     * 函数功能：判断操作符是否为逻辑运算符。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isLogical() {
        return category() == OperatorCategory.LOGICAL;
    }

    /**
     * 函数功能：判断操作符是否为相等性运算符。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isEquality() {
        return category() == OperatorCategory.EQUALITY;
    }

    /**
     * 函数功能：判断操作符是否为关系运算符。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isRelational() {
        return category() == OperatorCategory.RELATIONAL;
    }

    /**
     * 函数功能：判断操作符是否为算术运算符。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isArithmetic() {
        return category() == OperatorCategory.ARITHMETIC;
    }
}
