package org.harvey.vie.theory.semantic.log;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceErrorType;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 03:21
 */
@Slf4j
public class TreeLogCallback implements ShiftReduceCallback {
    @Override
    public void onAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        log.info("Stack:" + context.getTreeContext());
        ShiftReduceCallback.super.onAccept(context, production);
    }

    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        log.debug("Stack:" + context.getTreeContext());
        ShiftReduceCallback.super.onReduce(context, production);
    }

    @Override
    public void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token) {
        log.debug("Stack:" + context.getTreeContext());
        ShiftReduceCallback.super.onShift(context, nextStatus, token);
    }

    @Override
    public void onError(ShiftReduceSemanticContext context, ShiftReduceErrorType errorType) {
        log.error("type: " + errorType);
        ShiftReduceCallback.super.onError(context, errorType);
    }
}
