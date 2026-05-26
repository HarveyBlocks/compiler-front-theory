package org.harvey.vie.theory.semantic.type;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

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
        BOOLEAN, CHARACTER, INT32, FLOAT64, STRING, VOID, STRUCT, NULL;
    }

    private final Kind kind;
    private final List<Integer> dimensions;
    private final IdentifierKey namedTypeKey;

    private SemanticType(Kind kind, List<Integer> dimensions, IdentifierKey namedTypeKey) {
        this.kind = kind;
        this.dimensions = List.copyOf(dimensions);
        this.namedTypeKey = namedTypeKey;
    }

    public static SemanticType scalar(Kind kind) {
        return new SemanticType(kind, List.of(), null);
    }

    public static SemanticType array(Kind kind, List<Integer> dimensions) {
        return new SemanticType(kind, dimensions, null);
    }

    public static SemanticType struct(SourceToken token) {
        return new SemanticType(Kind.STRUCT, List.of(), IdentifierKey.generate(token));
    }

    public static SemanticType nullLiteral() {
        return scalar(Kind.NULL);
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

    public boolean isStruct() {
        return kind == Kind.STRUCT && isScalar();
    }

    public boolean isVoidScalar() {
        return kind == Kind.VOID && isScalar();
    }

    public boolean isNullLiteral() {
        return kind == Kind.NULL && isScalar();
    }

    public boolean isReferenceType() {
        return kind == Kind.STRUCT || isArray();
    }

    public SemanticType withAppendedDimension(int dimension) {
        ArrayList<Integer> next = new ArrayList<>(dimensions);
        next.add(dimension);
        return new SemanticType(kind, next, namedTypeKey);
    }

    public SemanticType withAppendedDimension() {
        return withAppendedDimension(0);
    }

    public SemanticType arrayElementType() {
        if (!isArray()) {
            throw new IllegalStateException("array element type requires an array semantic type");
        }
        if (dimensions.size() == 1) {
            return new SemanticType(kind, List.of(), namedTypeKey);
        }
        return new SemanticType(kind, dimensions.subList(1, dimensions.size()), namedTypeKey);
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
        joiner.add(kind.name().toLowerCase());
        for (int i = 0; i < dimensions.size(); i++) {
            joiner.add("[");
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
        return kind == that.kind &&
               Objects.equals(dimensions, that.dimensions) &&
               Objects.equals(namedTypeKey, that.namedTypeKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, dimensions, namedTypeKey);
    }
}
