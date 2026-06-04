package org.harvey.vie.theory.semantic.callback.td;

import org.harvey.vie.theory.semantic.callback.SemanticCallback;
import org.harvey.vie.theory.semantic.context.PredictiveSemanticContext;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 12:24
 */
public interface PredictiveCallback extends SemanticCallback {
    /**
     * 函数功能：处理语义分析开始事件。
     * 输入：
     * - ctx：PredictiveSemanticContext 类型参数。
     * 输出：无。
     */

    void onStart(PredictiveSemanticContext ctx);

    /**
     * 函数功能：处理终结符匹配事件。
     * 输入：
     * - ctx：PredictiveSemanticContext 类型参数。
     * - terminal：TerminalSymbol 类型参数。
     * 输出：无。
     */

    void onTerminal(PredictiveSemanticContext ctx, TerminalSymbol terminal);

    /**
     * 函数功能：处理空产生式事件。
     * 输入：
     * - ctx：PredictiveSemanticContext 类型参数。
     * - head：HeadSymbol 类型参数。
     * 输出：无。
     */

    void onEpsilonProduction(PredictiveSemanticContext ctx, HeadSymbol head);

    /**
     * 函数功能：处理产生式规约事件。
     * 输入：
     * - ctx：PredictiveSemanticContext 类型参数。
     * - concatenation：GrammarConcatenation 类型参数。
     * 输出：无。
     */

    void onProduction(PredictiveSemanticContext ctx, GrammarConcatenation concatenation);

    /**
     * 函数功能：处理接受事件。
     * 输入：
     * - ctx：PredictiveSemanticContext 类型参数。
     * 输出：无。
     */

    void onAccept(PredictiveSemanticContext ctx);

    /**
     * 函数功能：处理语义或语法错误事件。
     * 输入：
     * - ctx：PredictiveSemanticContext 类型参数。
     * - errorType：PredicativeErrorType 类型参数。
     * 输出：无。
     */

    void onError(PredictiveSemanticContext ctx, PredicativeErrorType errorType);

    /**
     * 函数功能：处理接受前事件。
     * 输入：
     * - ctx：PredictiveSemanticContext 类型参数。
     * 输出：无。
     */

    void beforeAccept(PredictiveSemanticContext ctx);
}
