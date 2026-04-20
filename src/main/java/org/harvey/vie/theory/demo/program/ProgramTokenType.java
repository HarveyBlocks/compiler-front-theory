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
    CONTROL_STRUCTURES_CONTINUE(2);

    private final int priority;

    ProgramTokenType(int priority) {this.priority = priority;}

    @Override
    public int store(OutputStream os) throws IOException {
        return Storages.storeInteger(os, ordinal());
    }

    public static class Loader implements TokenType.Loader<ProgramTokenType> {

        /**
         * @throws IOException null for unknown token
         */
        @Override
        public ProgramTokenType load(InputStream is) throws IOException {
            int id = Loaders.loadInteger(is);
            return id < 0 ? null : ProgramTokenType.values()[id];
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public @NonNull String hint() {
        return name();
    }
}
