package org.harvey.vie.theory.semantic.command.translator.command;

/**
 * 运算符语义类别，用于表达式翻译阶段选择类型检查规则。
 * <p>
 * {@link InSuffixExpressionTranslator} 会按这些类别区分：逻辑运算要求 boolean，
 * 关系和算术运算要求数值，相等运算允许同类型或可比较数值类型。
 *
 * @author Temper
 */
public enum OperatorCategory {
    LOGICAL,
    EQUALITY,
    RELATIONAL,
    ARITHMETIC,
    UNARY
}
