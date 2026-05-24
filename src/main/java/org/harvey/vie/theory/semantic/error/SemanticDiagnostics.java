package org.harvey.vie.theory.semantic.error;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.SemanticType;

/**
 * Shared semantic type validations used by translators.
 *
 * @author Temper
 */
public final class SemanticDiagnostics {
    private SemanticDiagnostics() {
    }

    public static void requireBoolean(
            ShiftReduceSemanticContext context,
            SemanticType type,
            SourceToken token,
            String message) {
        if (!type.isBooleanScalar()) {
            reject(context, token, message);
        }
    }

    public static void requireNumeric(
            ShiftReduceSemanticContext context,
            SemanticType type,
            SourceToken token,
            String message) {
        if (!type.isNumericScalar()) {
            reject(context, token, message);
        }
    }

    public static void requireAssignable(
            ShiftReduceSemanticContext context,
            SemanticType sourceType,
            SemanticType targetType,
            SourceToken token,
            String message) {
        if (!context.canImplicitlyConvert(sourceType, targetType)) {
            reject(context, token, message);
        }
    }

    public static void requireNotVoid(
            ShiftReduceSemanticContext context,
            SemanticType type,
            SourceToken token,
            String message) {
        if (type != null && type.isVoidScalar()) {
            reject(context, token, message);
        }
    }

    public static void reject(
            ShiftReduceSemanticContext context,
            SourceToken token,
            String message) {
        context.addError(token.getOffset(), message);
        throw new CompilerException(message);
    }
}
