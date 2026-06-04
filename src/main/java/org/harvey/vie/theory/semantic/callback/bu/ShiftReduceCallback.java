package org.harvey.vie.theory.semantic.callback.bu;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.SemanticCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 12:22
 */
public interface ShiftReduceCallback extends SemanticCallback {
    /**
     * 函数功能：处理语义分析开始事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：无。
     */
    default void onStart(ShiftReduceSemanticContext context) {
        context.onStart();
    }

    /**
     * 函数功能：处理接受前事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */
    default void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        context.beforeAccept(production);
    }
/**
 * 函数功能：处理接受事件。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：无。
 */

    default void onAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        context.onAccept(production);
    }
/**
 * 函数功能：处理规约事件。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - production：SimpleGrammarProduction 类型参数。
 * 输出：无。
 */

    default void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        context.onReduce(production);
    }
/**
 * 函数功能：处理移进事件。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - nextStatus：int 类型参数。
 * - token：SourceToken 类型参数。
 * 输出：无。
 */

    default void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token) {
        context.onShift(nextStatus, token);
    }
/**
 * 函数功能：处理语义或语法错误事件。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - errorType：ShiftReduceErrorType 类型参数。
 * 输出：无。
 */

    default void onError(ShiftReduceSemanticContext context, ShiftReduceErrorType errorType) {
        context.onError(errorType);
    }
}
