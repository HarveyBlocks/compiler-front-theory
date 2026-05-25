package org.harvey.vie.theory.semantic.value;

import lombok.Getter;
import org.harvey.vie.theory.semantic.type.SemanticType;

/**
 * Compile-time constant value bound to a syntax node.
 *
 * @author Temper
 */
@Getter
public class ConstantValue {
    private final SemanticType type;
    private final Object value;

    public ConstantValue(SemanticType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public int int32() {
        return (Integer) value;
    }

    public double float64() {
        return (Double) value;
    }

    public boolean bool() {
        return (Boolean) value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

