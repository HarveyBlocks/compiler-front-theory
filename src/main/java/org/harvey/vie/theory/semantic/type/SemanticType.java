package org.harvey.vie.theory.semantic.type;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;


/**
 * @author Temper
 */
@Getter
public final class SemanticType {
    public enum Kind {
        BOOLEAN, CHARACTER, INT32, FLOAT64, STRING, VOID;

    }

    private final Kind kind;
    private final List<Integer> dimensions;

    private SemanticType(Kind kind, List<Integer> dimensions) {
        this.kind = kind;
        this.dimensions = List.copyOf(dimensions);
    }

    public static SemanticType scalar(Kind kind) {
        return new SemanticType(kind, List.of());
    }

    public static SemanticType array(Kind kind, List<Integer> dimensions) {
        return new SemanticType(kind, dimensions);
    }

    public boolean isScalar() {
        return dimensions.isEmpty();
    }

    public boolean isArray() {
        return !dimensions.isEmpty();
    }

    public boolean isBooleanScalar() {
        return kind == Kind.BOOLEAN && isScalar();
    }

    public boolean isNumericScalar() {
        return isScalar() && (kind == Kind.INT32 || kind == Kind.FLOAT64);
    }

    public boolean isVoidScalar() {
        return kind == Kind.VOID && isScalar();
    }

    public SemanticType withAppendedDimension(int dimension) {
        ArrayList<Integer> next = new ArrayList<>(dimensions);
        next.add(dimension);
        return new SemanticType(kind, next);
    }

    public SemanticType arrayElementType() {
        if (!isArray()) {
            throw new IllegalStateException("array element type requires an array semantic type");
        }
        if (dimensions.size() == 1) {
            return scalar(kind);
        }
        return new SemanticType(kind, dimensions.subList(1, dimensions.size()));
    }

    public SemanticType commonNumericType(SemanticType other) {
        if (!isNumericScalar() || !other.isNumericScalar()) {
            throw new IllegalStateException("common numeric type requires numeric scalar operands");
        }
        return kind == Kind.FLOAT64 || other.kind == Kind.FLOAT64 ? scalar(Kind.FLOAT64) : scalar(Kind.INT32);
    }

    public boolean canImplicitlyCastTo(SemanticType target) {
        return equals(target) || isNumericScalar() && target.isNumericScalar();

    }


    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(kind.name());
        for (Integer dimension : dimensions) {
            joiner.add("[");
            joiner.add(String.valueOf(dimension));
            joiner.add("]");
        }
        return joiner.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SemanticType)) {
            return false;
        }
        SemanticType that = (SemanticType) object;
        return kind == that.kind && Objects.equals(dimensions, that.dimensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, dimensions);
    }
}
