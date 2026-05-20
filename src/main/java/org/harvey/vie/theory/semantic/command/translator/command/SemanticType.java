package org.harvey.vie.theory.semantic.command.translator.command;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public final class SemanticType {
    public enum Kind {
        BOOLEAN,
        CHARACTER,
        INT32,
        FLOAT64,
        STRING,
        UNKNOWN
    }

    private static final SemanticType UNKNOWN = new SemanticType(Kind.UNKNOWN, 0, List.of());

    private final Kind kind;
    private final int arrayRank;
    private final List<Integer> dimensions;

    private SemanticType(Kind kind, int arrayRank, List<Integer> dimensions) {
        this.kind = kind;
        this.arrayRank = arrayRank;
        this.dimensions = List.copyOf(dimensions);
    }

    public static SemanticType unknown() {
        return UNKNOWN;
    }

    public static SemanticType scalar(Kind kind) {
        return new SemanticType(kind, 0, List.of());
    }

    public static SemanticType array(Kind kind, List<Integer> dimensions) {
        return new SemanticType(kind, dimensions.size(), dimensions);
    }

    public Kind getKind() {
        return kind;
    }

    public int getArrayRank() {
        return arrayRank;
    }

    public boolean isUnknown() {
        return kind == Kind.UNKNOWN;
    }

    public boolean isScalar() {
        return !isUnknown() && arrayRank == 0;
    }

    public boolean isArray() {
        return arrayRank > 0;
    }

    public boolean isBooleanScalar() {
        return kind == Kind.BOOLEAN && arrayRank == 0;
    }

    public boolean isNumericScalar() {
        return arrayRank == 0 && (kind == Kind.INT32 || kind == Kind.FLOAT64);
    }

    public SemanticType elementType() {
        if (!isArray()) {
            return unknown();
        }
        if (arrayRank == 1) {
            return scalar(kind);
        }
        return new SemanticType(kind, arrayRank - 1, dimensions.subList(1, dimensions.size()));
    }

    public SemanticType promoteNumeric(SemanticType other) {
        if (!isNumericScalar() || !other.isNumericScalar()) {
            return unknown();
        }
        if (kind == Kind.FLOAT64 || other.kind == Kind.FLOAT64) {
            return scalar(Kind.FLOAT64);
        }
        return scalar(Kind.INT32);
    }

    @Override
    public String toString() {
        if (isUnknown()) {
            return "unknown";
        }
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(kindName(kind));
        for (Integer dimension : dimensions) {
            joiner.add("[").add(String.valueOf(dimension)).add("]");
        }
        return joiner.toString();
    }

    private static String kindName(Kind kind) {
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
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SemanticType)) {
            return false;
        }
        SemanticType that = (SemanticType) object;
        return arrayRank == that.arrayRank && kind == that.kind && Objects.equals(dimensions, that.dimensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, arrayRank, dimensions);
    }
}
