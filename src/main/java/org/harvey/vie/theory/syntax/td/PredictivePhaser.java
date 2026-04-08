package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.SemanticResult;
import org.harvey.vie.theory.syntax.callback.PredictiveCallbackRegister;

/**
 * TODO 预测分析
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:16
 */
public interface PredictivePhaser {

    SemanticResult phase(SourceTokenIterator iterator, ErrorContext errorContext);
}
