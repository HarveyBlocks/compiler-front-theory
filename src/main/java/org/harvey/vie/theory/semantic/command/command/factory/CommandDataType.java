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
