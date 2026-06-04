package org.harvey.vie.theory.demo.program;

import lombok.NonNull;
import org.harvey.vie.theory.io.Loaders;
import org.harvey.vie.theory.io.Storages;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-13 20:39
 */
public enum ProgramTokenType implements TokenType {
    // identifier的优先级要偏低. 为了避免和关键字冲突
    IDENTIFIER(10),
    SPACE(9),
    COMMENT_BLOCK(8),
    COMMENT_LINE(8),
    CONSTANT_STRING(7),
    CONSTANT_CHARACTER(7),
    CONSTANT_INTEGER(6),
    CONSTANT_FLOAT(6),
    CONSTANT_BOOLEAN_TRUE(5),
    CONSTANT_BOOLEAN_FALSE(5),
    TYPE_BOOLEAN(4),
    TYPE_CHARACTER(4),
    TYPE_INT32(4),
    TYPE_FLOAT64(4),
    TYPE_STRING(4),
    KEYWORD_NEW(4),
    KEYWORD_STRUCT(4),
    OPERATOR_PLUS(3),
    OPERATOR_MULTIPLY(3),
    OPERATOR_PARENTHESIS_OPEN(3),
    OPERATOR_PARENTHESIS_CLOSE(3),
    OPERATOR_ASSIGN(3),
    OPERATOR_SEMICOLON(3),
    OPERATOR_SQUARE_OPEN(3),
    OPERATOR_SQUARE_CLOSE(3),
    OPERATOR_BRACE_OPEN(3),
    OPERATOR_BRACE_CLOSE(3),
    CONTROL_STRUCTURES_IF(2),
    CONTROL_STRUCTURES_ELSE(2),
    CONTROL_STRUCTURES_WHILE(2),
    CONTROL_STRUCTURES_DO(2),
    CONTROL_STRUCTURES_BREAK(2),
    CONTROL_STRUCTURES_CONTINUE(2),
    OPERATOR_MINUS(3),
    OPERATOR_DIVIDE(3),
    OPERATOR_LOGICAL_NOT(3),
    OPERATOR_LOGICAL_AND(3),
    OPERATOR_LOGICAL_OR(3),
    OPERATOR_EQUAL(3),
    OPERATOR_NOT_EQUAL(3),
    OPERATOR_LESS(3),
    OPERATOR_LESS_EQUAL(3),
    OPERATOR_GREATER(3),
    OPERATOR_GREATER_EQUAL(3),
    TYPE_VOID(4),
    CONSTANT_NULL(5),
    OPERATOR_COMMA(3),
    OPERATOR_DOT(3),
    CONTROL_STRUCTURES_RETURN(2),
    TYPE_IDENTIFIER(10);

    private final int priority;

    /**
     * 函数功能：创建带词法优先级的程序词法单元类型。
     * 输入：
     * - priority：词法单元匹配优先级。
     * 输出：无。
     */
    ProgramTokenType(int priority) {this.priority = priority;}

    /**
     * 函数功能：将程序词法单元类型编号写入输出流。
     * 输入：
     * - os：接收序列化数据的输出流。
     * 输出：写入的字节数。
     */
    @Override
    public int store(OutputStream os) throws IOException {
        return Storages.storeInteger(os, ordinal());
    }

    public static class Loader implements TokenType.Loader<ProgramTokenType> {

        /**
         * 函数功能：从输入流读取编号并还原程序词法单元类型。
         * 输入：
         * - is：包含序列化编号的输入流。
         * 输出：编号对应的 ProgramTokenType；负数编号返回 null。
         */
        @Override
        public ProgramTokenType load(InputStream is) throws IOException {
            int id = Loaders.loadInteger(is);
            return id < 0 ? null : ProgramTokenType.values()[id];
        }
    }

    /**
     * 函数功能：获取程序词法单元类型的匹配优先级。
     * 输入：
     * - 无。
     * 输出：词法单元优先级整数。
     */
    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * 函数功能：获取程序词法单元类型的提示名称。
     * 输入：
     * - 无。
     * 输出：词法单元类型名称字符串。
     */
    @Override
    public @NonNull String hint() {
        return name();
    }
}
