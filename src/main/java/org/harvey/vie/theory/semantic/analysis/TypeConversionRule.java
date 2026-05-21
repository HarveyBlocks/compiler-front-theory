package org.harvey.vie.theory.semantic.analysis;

// TODO 意义不明, 为什么要专门写? 意义呢? 专门用来干嘛的? 说清楚了吗?
public class TypeConversionRule {
    public boolean canImplicitlyConvert(SemanticType from, SemanticType to) {
        if (from.isUnknown() || to.isUnknown()) {
            return false;
        }
        if (from.equals(to)) {
            return true;
        }
        if (!from.isNumericScalar() || !to.isNumericScalar()) {
            return false;
        }
        return from.getKind() == SemanticType.Kind.INT32 && to.getKind() == SemanticType.Kind.FLOAT64;
    }

    public boolean requiresImplicitCast(SemanticType from, SemanticType to) {
        return canImplicitlyConvert(from, to) && !from.equals(to);
    }

    public SemanticType commonBinaryType(SemanticType left, SemanticType right) {
        if (!left.isNumericScalar() || !right.isNumericScalar()) {
            return SemanticType.unknown();
        }
        if (left.getKind() == SemanticType.Kind.FLOAT64 || right.getKind() == SemanticType.Kind.FLOAT64) {
            return SemanticType.scalar(SemanticType.Kind.FLOAT64);
        }
        return SemanticType.scalar(SemanticType.Kind.INT32);
    }
}
