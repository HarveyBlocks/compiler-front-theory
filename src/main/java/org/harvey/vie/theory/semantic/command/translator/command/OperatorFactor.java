package org.harvey.vie.theory.semantic.command.translator.command;

/**
 * 运算符描述对象：把语义标签里的运算符转换成命令工厂需要的助记符和类别。
 * <p>
 * {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose} 为每个运算标签创建一个
 * {@link OperatorFactor}，例如 {@code PLUS} 对应 {@code plus} 和 {@link OperatorCategory#ARITHMETIC}。
 * {@link InSuffixExpressionTranslator} 和 {@link UnaryExpressionTranslator} 用它决定类型检查规则和最终命令名。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:14
 */
public interface OperatorFactor {
    String mnemonic();

    OperatorCategory category();

    default boolean isLogical() {
        return category() == OperatorCategory.LOGICAL;
    }

    default boolean isEquality() {
        return category() == OperatorCategory.EQUALITY;
    }

    default boolean isRelational() {
        return category() == OperatorCategory.RELATIONAL;
    }

    default boolean isArithmetic() {
        return category() == OperatorCategory.ARITHMETIC;
    }
}
