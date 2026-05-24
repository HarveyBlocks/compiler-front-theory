package org.harvey.vie.theory.semantic.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.semantic.type.SemanticType;

/**
 * Compile-time constant value bound to a syntax node.
 */
@Getter
@AllArgsConstructor
public class ConstantValue {
    private final SemanticType type;
    private final Object value;

    public int int32() {
        return (Integer) value;
    }

    public double float64() {
        return (Double) value;
    }

    public boolean bool() {
        return (Boolean) value;
    }

    public String literalText() {
        switch (type.getKind()) {
            case BOOLEAN:
                return bool() ? "true" : "false";
            case INT32:
                return String.valueOf(int32());
            case FLOAT64:
                return Double.toString(float64());
            default:
                throw new IllegalStateException("unsupported constant literal type: " + type);
        }
    }
}
