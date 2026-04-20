package org.harvey.vie.theory.semantic.error;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceErrorType;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 04:25
 */
public class PassiveErrorCallback implements ShiftReduceCallback {
    @Override
    public void onError(ShiftReduceSemanticContext context, ShiftReduceErrorType errorType) {
        ShiftReduceCallback.super.onError(context, errorType);
        throw new CompilerException("Interrupt on syntax error");
    }
}
