package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.semantic.type.SemanticType;

/**
 * Instruction-level data category used by command encoding.
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
/**
 * 函数功能：创建 CommandDataType 对象。
 * 输入：
 * - mnemonic：String 类型参数。
 * 输出：无。
 */

    CommandDataType(String mnemonic) {
        this.mnemonic = mnemonic;
    }
/**
 * 函数功能：获取操作符助记符。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    public String mnemonic() {
        return mnemonic;
    }
/**
 * 函数功能：获取存储类型对应的命令数据类型。
 * 输入：
 * - type：SemanticType 类型参数。
 * 输出：CommandDataType 类型返回值。
 */

    public static CommandDataType forStorage(SemanticType type) {
        if (type.isReferenceType()) {
            return REF;
        }
        return forScalar(type);
    }
/**
 * 函数功能：获取值类型对应的命令数据类型。
 * 输入：
 * - type：SemanticType 类型参数。
 * 输出：CommandDataType 类型返回值。
 */

    public static CommandDataType forValue(SemanticType type) {
        if (type.isReferenceType()) {
            throw new IllegalArgumentException("reference type does not have a scalar command value category: " + type);
        }
        return forScalar(type);
    }
/**
 * 函数功能：获取标量类型对应的命令数据类型。
 * 输入：
 * - type：SemanticType 类型参数。
 * 输出：CommandDataType 类型返回值。
 */

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
