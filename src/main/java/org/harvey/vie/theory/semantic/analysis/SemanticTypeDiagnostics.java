package org.harvey.vie.theory.semantic.analysis;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * Shared semantic type validations used by translators.
 */
public final class SemanticTypeDiagnostics {
    private SemanticTypeDiagnostics() {
    }

    public static void requireBoolean(
            ShiftReduceSemanticContext context,
            SemanticType type,
            SourceToken token,
            String message) {
        if (!type.isUnknown() && !type.isBooleanScalar()) {
            reject(context, token, message);
        }
    }

    public static void requireNumeric(
            ShiftReduceSemanticContext context,
            SemanticType type,
            SourceToken token,
            String message) {
        if (!type.isUnknown() && !type.isNumericScalar()) {
            reject(context, token, message);
        }
    }

    public static void requireAssignable(
            ShiftReduceSemanticContext context,
            SemanticType sourceType,
            SemanticType targetType,
            SourceToken token,
            String message) {
        if (!sourceType.isUnknown() &&
            !targetType.isUnknown() &&
            !context.getTypeSystem().canImplicitlyConvert(sourceType, targetType)) {
            reject(context, token, message);
        }
    }

    public static void reject(
            ShiftReduceSemanticContext context,
            SourceToken token,
            String message) {
        if (token != null) {
            context.addError(token.getOffset(), message);
        }
        throw new CompilerException(message);
    }
}
