package org.harvey.vie.theory.demo.program;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeResolver;

/**
 * @author Temper
 */
public class ProgramTypeResolver implements TypeResolver {
    /**
     * 函数功能：解析字面量词法单元对应的语义类型。
     * 输入：
     * - token：字面量对应的源词法单元。
     * 输出：字面量对应的 SemanticType。
     */
    @Override
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
            case CONSTANT_NULL:
                return SemanticType.nullLiteral();
            default:
                throw new IllegalArgumentException("token does not represent a literal type: " + tokenType);
        }
    }

    /**
     * 函数功能：解析类型词法单元对应的语义类型。
     * 输入：
     * - token：类型对应的源词法单元。
     * 输出：类型词法单元对应的 SemanticType。
     */
    @Override
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
            case TYPE_IDENTIFIER:
                return SemanticType.struct(token);
            default:
                throw new IllegalArgumentException("token does not represent a declared type: " + tokenType);
        }
    }

    /**
     * 函数功能：获取源词法单元的程序词法类型。
     * 输入：
     * - token：源词法单元。
     * 输出：ProgramTokenType 类型；词法单元为空时返回 null。
     */
    private ProgramTokenType tokenType(SourceToken token) {
        return token == null ? null : (ProgramTokenType) token.getType();
    }
}
