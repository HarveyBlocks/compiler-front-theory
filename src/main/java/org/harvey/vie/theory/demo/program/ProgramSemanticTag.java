package org.harvey.vie.theory.demo.program;

import org.harvey.vie.theory.io.Loaders;
import org.harvey.vie.theory.io.Storages;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO 一个Production对应0-n个Tag
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 16:19
 */
public enum ProgramSemanticTag implements SemanticTag {
    FUNCTION,
    HEAD,
    DECLARATION,
    CALL,
    RETURN,
    VALUE,
    PARAMETER,
    ARGUMENT,
    LIST,
    EMPTY,
    BLOCK,
    IDENTIFIER,
    USE,
    INITIALIZED,
    COMMAND,
    PROGRAM,
    NOOP,
    SEQUENCE,
    TYPE,
    ARRAY,
    PARENTHESIZED,
    LEFT_VALUE,
    ASSIGNMENT,
    ACCESS,
    CONDITIONAL,
    ELSE_BRANCH,
    LOOP,
    DO_LOOP,
    UNARY,
    BINARY,
    OR,
    AND,
    EQUAL,
    NOT_EQUAL,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    LOGICAL_NOT,
    NEGATE;

    @Override
    public int store(OutputStream os) throws IOException {
        return Storages.storeInteger(os, this.ordinal());
    }

    public static class Loader implements SemanticTag.Loader<ProgramSemanticTag> {
        @Override
        public ProgramSemanticTag load(InputStream is) throws IOException {
            return ProgramSemanticTag.values()[Loaders.loadInteger(is)];
        }
    }
}
