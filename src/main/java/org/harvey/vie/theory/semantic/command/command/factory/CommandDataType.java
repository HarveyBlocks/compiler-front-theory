package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.semantic.type.SemanticType;

/**
 * 命令层面的数据类别。
 * <p>
 * 它不是源语言完整类型系统，而是把 {@link SemanticType} 压缩成命令后缀需要的几类：
 * 标量保留为 {@code boolean}/{@code int32}/{@code float64} 等，数组和结构体统一按 {@link #REF}
 * 处理。这样命令文本可以稳定写成 {@code load_st_ref_address}、{@code assign_from_st_top_to_ref_int32}
 * 等形式。
 * <p>
 * 讲完本枚举回到 {@link CommandFactory} 或
 * {@link org.harvey.vie.theory.semantic.command.command.string.TypedStringCommandFactory}。
 *
 * @author Temper
 */
public enum CommandDataType {
    BOOLEAN("boolean"),
    CHARACTER("character"),
    INT32("int32"),
    FLOAT64("float64"),
    STRING("string"),
    NULL("null"),
    REF("ref");

    private final String mnemonic;

    CommandDataType(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String mnemonic() {
        return mnemonic;
    }

    public static CommandDataType forStorage(SemanticType type) {
        if (type.isReferenceType()) {
            return REF;
        }
        return forScalar(type);
    }

    public static CommandDataType forValue(SemanticType type) {
        if (type.isReferenceType()) {
            throw new IllegalArgumentException("reference type does not have a scalar command value category: " + type);
        }
        return forScalar(type);
    }

    private static CommandDataType forScalar(SemanticType type) {
        switch (type.getKind()) {
            case BOOLEAN:
                return BOOLEAN;
            case CHARACTER:
                return CHARACTER;
            case INT32:
                return INT32;
            case FLOAT64:
                return FLOAT64;
            case STRING:
                return STRING;
            case NULL:
                return NULL;
            case STRUCT:
                return REF;
            case VOID:
                throw new IllegalArgumentException("void cannot be encoded as a command data category");
            default:
                throw new IllegalStateException("unsupported semantic type kind: " + type.getKind());
        }
    }
}
