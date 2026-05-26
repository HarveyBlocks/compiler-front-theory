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
    // Root and callable semantics
    PROGRAM,
    FUNCTION,
    HEAD,
    CALL,
    RETURN,
    PARAMETER,
    ARGUMENT,

    // Structure declarations
    STRUCT_DECL,
    STRUCT_FIELD,

    // Type construction and declarations
    DECLARATION,
    TYPE,
    STRUCT_TYPE,
    ARRAY,
    ARRAY_CREATION_BASE,
    ARRAY_CREATION_DIM,

    // Command and control flow
    BLOCK,
    ITEM,
    STATEMENT,
    COMMAND,
    CONDITIONAL,
    ELSE_BRANCH,
    LOOP,
    DO_LOOP,
    ASSIGNMENT,

    // Value/category semantics
    ACCESS,
    MEMBER_ACCESS,
    LEFT_VALUE,
    PARENTHESIZED,
    NEW_STRUCT,
    NEW_ARRAY,
    LITERAL,
    NULL_LITERAL,

    VALUE,
    IDENTIFIER,
    USE,
    INITIALIZED,

    // Structural helper tags
    LIST,
    EMPTY,
    NOOP,
    SEQUENCE,
    FORWARD,

    // Operator semantics
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
