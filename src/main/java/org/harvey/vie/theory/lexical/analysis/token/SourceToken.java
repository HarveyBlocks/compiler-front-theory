package org.harvey.vie.theory.lexical.analysis.token;

/**
 * Interface representing a lexical token extracted from the source code.
 * A token is the smallest meaningful unit for the subsequent parsing phase.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:13
 */
public interface SourceToken {
    /**
     * 函数功能：获取源词法单元的提示字符串。
     * 输入：
     * - 无。
     * 输出：源词法单元提示字符串。
     */
    String hintString();

    /**
     * 函数功能：获取源词法单元的词素字节数组。
     * 输入：
     * - 无。
     * 输出：词素 byte 数组。
     */
    byte[] getLexeme();

    /**
     * 函数功能：获取源词法单元的偏移量。
     * 输入：
     * - 无。
     * 输出：词法单元偏移量整数。
     */
    int getOffset();

    /**
     * 函数功能：获取源词法单元的类型。
     * 输入：
     * - 无。
     * 输出：源词法单元的 TokenType。
     */
    TokenType getType();
}
