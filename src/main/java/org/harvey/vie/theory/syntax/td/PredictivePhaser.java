package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.context.SemanticResult;

/**
 * TODO 预测分析
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:16
 */
public interface PredictivePhaser {
    /**
     * 函数功能：执行语法分析并返回语义结果。
     * 输入：
     * - iterator：SourceTokenIterator 类型参数。
     * - errorContext：ErrorContext 类型参数。
     * 输出：SemanticResult 类型返回值。
     */

    SemanticResult phase(SourceTokenIterator iterator, ErrorContext errorContext);
}
