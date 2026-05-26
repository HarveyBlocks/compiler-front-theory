package org.harvey.vie.theory.semantic.command.translator.command;

/**
 * Strongly-typed operator descriptor used by command translators.
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
