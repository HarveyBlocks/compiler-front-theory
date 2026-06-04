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
    /**
     * 函数功能：创建 SemanticDiagnostics 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private SemanticDiagnostics() {
    }
/**
 * 函数功能：校验表达式类型是否为布尔类型。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - type：SemanticType 类型参数。
 * - token：SourceToken 类型参数。
 * - message：String 类型参数。
 * 输出：无。
 */

    public static void requireBoolean(
            ShiftReduceSemanticContext context,
            SemanticType type,
            SourceToken token,
            String message) {
        if (!type.isBooleanScalar()) {
            reject(context, token, message);
        }
    }
/**
 * 函数功能：校验表达式类型是否为数值类型。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - type：SemanticType 类型参数。
 * - token：SourceToken 类型参数。
 * - message：String 类型参数。
 * 输出：无。
 */

    public static void requireNumeric(
            ShiftReduceSemanticContext context,
            SemanticType type,
            SourceToken token,
            String message) {
        if (!type.isNumericScalar()) {
            reject(context, token, message);
        }
    }
/**
 * 函数功能：校验表达式是否可赋值。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - sourceType：SemanticType 类型参数。
 * - targetType：SemanticType 类型参数。
 * - token：SourceToken 类型参数。
 * - message：String 类型参数。
 * 输出：无。
 */

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
/**
 * 函数功能：校验表达式类型不是 void。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - type：SemanticType 类型参数。
 * - token：SourceToken 类型参数。
 * - message：String 类型参数。
 * 输出：无。
 */

    public static void requireNotVoid(
            ShiftReduceSemanticContext context,
            SemanticType type,
            SourceToken token,
            String message) {
        if (type != null && type.isVoidScalar()) {
            reject(context, token, message);
        }
    }
/**
 * 函数功能：报告语义错误并中断。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - token：SourceToken 类型参数。
 * - message：String 类型参数。
 * 输出：无。
 */

    public static void reject(
            ShiftReduceSemanticContext context,
            SourceToken token,
            String message) {
        context.addError(token.getOffset(), message);
        throw new CompilerException(message);
    }
}
