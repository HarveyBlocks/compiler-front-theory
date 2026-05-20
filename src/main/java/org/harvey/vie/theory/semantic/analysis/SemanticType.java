package org.harvey.vie.theory.semantic.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public final class SemanticType {
    public enum Kind {
        BOOLEAN, CHARACTER, INT32, FLOAT64, STRING, UNKNOWN
    }

    private static final SemanticType UNKNOWN = new SemanticType(Kind.UNKNOWN, List.of());

    private final Kind kind;
    private final List<Integer> dimensions;

    private SemanticType(Kind kind, List<Integer> dimensions) {
        this.kind = kind;
        this.dimensions = List.copyOf(dimensions);
    }

    public static SemanticType unknown() {
        // TODO 任何对Unknown的提供都是不负责任的,
        //  本质上是拖延了对错误的判断, 最终将找不到真正的错误在哪里
        return UNKNOWN;
    }

    public static SemanticType scalar(Kind kind) {
        return new SemanticType(kind, List.of());
    }

    public static SemanticType array(Kind kind, List<Integer> dimensions) {
        return new SemanticType(kind, dimensions);
    }

    public Kind getKind() {
        return kind;
    }

    public List<Integer> getDimensions() {
        return dimensions;
    }

    public boolean isUnknown() {
        return kind == Kind.UNKNOWN;
    }

    public boolean isScalar() {
        return !isUnknown() && dimensions.isEmpty();
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

    public SemanticType withAppendedDimension(int dimension) {
        ArrayList<Integer> next = new ArrayList<>(dimensions);
        next.add(dimension);
        return new SemanticType(kind, next);
    }

    public SemanticType arrayElementType() {
        if (!isArray()) {
            return unknown();
        }
        if (dimensions.size() == 1) {
            return scalar(kind);
        }
        return new SemanticType(kind, dimensions.subList(1, dimensions.size()));
    }

    public SemanticType commonNumericType(SemanticType other) {
        if (!isNumericScalar() || !other.isNumericScalar()) {
            return unknown(); // TODO 又是开放了一个危险的 unknown
        }
        return kind == Kind.FLOAT64 || other.kind == Kind.FLOAT64 ? scalar(Kind.FLOAT64) : scalar(Kind.INT32);
    }

    public boolean canImplicitlyCastTo(SemanticType target) {
        return equals(target) || isNumericScalar() && target.isNumericScalar();

    }

    public String mnemonic() {
        // TODO 不合适的设计, 本质是解析字符串,
        //  通过JDK的字符串在类之间互相传递是没有丝毫可维护性的做法
        switch (kind) {
            case BOOLEAN:
                return "boolean";
            case CHARACTER:
                return "character";
            case INT32:
                return "int32";
            case FLOAT64:
                return "float64";
            case STRING:
                return "string";
            default:
                return "unknown";
        }
    }

    @Override
    public String toString() {
        if (isUnknown()) {
            return "unknown";
        }
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(mnemonic());
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
