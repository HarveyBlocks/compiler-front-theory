package org.harvey.vie.theory.semantic.analysis;

import org.harvey.vie.theory.demo.program.ProgramTokenType;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

import java.nio.charset.StandardCharsets;

public class TypeResolver {
    public SemanticType literalType(SourceToken token) {
        ProgramTokenType tokenType = tokenType(token);
        switch (tokenType) {
            case CONSTANT_BOOLEAN_TRUE:
            case CONSTANT_BOOLEAN_FALSE:
                return SemanticType.scalar(SemanticType.Kind.BOOLEAN);
            case CONSTANT_INTEGER:
                return SemanticType.scalar(SemanticType.Kind.INT32);
            case CONSTANT_FLOAT:
                return SemanticType.scalar(SemanticType.Kind.FLOAT64);
            case CONSTANT_CHARACTER:
                return SemanticType.scalar(SemanticType.Kind.CHARACTER);
            case CONSTANT_STRING:
                return SemanticType.scalar(SemanticType.Kind.STRING);
            default:
                throw new IllegalArgumentException("token does not represent a literal type: " + tokenType);
        }
    }

    public SemanticType typeToken(SourceToken token) {
        ProgramTokenType tokenType = tokenType(token);
        switch (tokenType) {
            case TYPE_BOOLEAN:
                return SemanticType.scalar(SemanticType.Kind.BOOLEAN);
            case TYPE_CHARACTER:
                return SemanticType.scalar(SemanticType.Kind.CHARACTER);
            case TYPE_INT32:
                return SemanticType.scalar(SemanticType.Kind.INT32);
            case TYPE_FLOAT64:
                return SemanticType.scalar(SemanticType.Kind.FLOAT64);
            case TYPE_STRING:
                return SemanticType.scalar(SemanticType.Kind.STRING);
            case TYPE_VOID:
                return SemanticType.scalar(SemanticType.Kind.VOID);
            default:
                throw new IllegalArgumentException("token does not represent a declared type: " + tokenType);
        }
    }

    public int integerLiteral(SourceToken token) {
        // TODO 直接使用Java的SDK进行Integer解析, 太过粗暴, 初始阶段勉强可以接受, 之后一定要改
        return Integer.parseInt(new String(token.getLexeme(), StandardCharsets.UTF_8));
    }

    private ProgramTokenType tokenType(SourceToken token) {
        return token == null ? null : (ProgramTokenType) token.getType();
    }
}
