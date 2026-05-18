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
    default void onStart(ShiftReduceSemanticContext context) {
        context.onStart();
    }

    /**
     * 到了可以accept的时机了. 本方法做一些accept的前置操作. 在本方法之后进行一系列判断, 保证能正确结束.
     */
    default void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        context.beforeAccept(production);
    }

    default void onAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        context.onAccept(production);
    }

    default void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        context.onReduce(production);
    }

    default void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token) {
        context.onShift(nextStatus, token);
    }

    default void onError(ShiftReduceSemanticContext context, ShiftReduceErrorType errorType) {
        context.onError(errorType);
    }
}
