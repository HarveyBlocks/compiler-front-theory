package org.harvey.vie.theory.semantic.callback;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaseContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 12:22
 */
public interface ShiftReduceCallback extends SemanticCallback {
    void onStart(ShiftReduceSemanticContext context);

    /**
     * 到了可以accept的时机了. 本方法做一些accept的前置操作. 在本方法之后进行一系列判断, 保证能正确结束.
     */
    void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production);

    void onAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production);

    void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production);

    void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token);

    void onError(ShiftReduceSemanticContext context, ShiftReduceErrorType errorType);
}
