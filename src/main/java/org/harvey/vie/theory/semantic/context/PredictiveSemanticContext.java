package org.harvey.vie.theory.semantic.context;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.td.PredicativeErrorType;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallback;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegister;
import org.harvey.vie.theory.syntax.SyntaxParsingContext;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-12 21:47
 */
@Getter
public class PredictiveSemanticContext {
    private final PredictiveCallbackRegister register;
    private final SyntaxParsingContext<GrammarUnitSymbol> context;
    @Setter
    private SemanticResult result;
    private Iterator<PredictiveCallback> callbackIter;

    /**
     * 函数功能：创建 PredictiveSemanticContext 对象。
     * 输入：
     * - register：PredictiveCallbackRegister 类型参数。
     * - context：SyntaxParsingContext<GrammarUnitSymbol> 类型参数。
     * 输出：无。
     */

    public PredictiveSemanticContext(
            PredictiveCallbackRegister register,
            SyntaxParsingContext<GrammarUnitSymbol> context) {
        this.register = register;
        this.context = context;
        callbackIter = register.iterator();
    }

    /**
     * 函数功能：执行空回调操作。
     * 输入：
     * - 无。
     * 输出：无。
     */

    private void invokeNothing() {
    }

    /**
     * 函数功能：处理语义分析开始事件。
     * 输入：
     * - 无。
     * 输出：无。
     */

    public void onStart() {
        registerNext(c -> c.onStart(this), this::invokeNothing);
    }

    /**
     * 函数功能：处理语义或语法错误事件。
     * 输入：
     * - predicativeErrorType：PredicativeErrorType 类型参数。
     * 输出：无。
     */

    public void onError(PredicativeErrorType predicativeErrorType) {
        registerNext(c -> c.onError(this, predicativeErrorType), this::invokeNothing);
        throw new RuntimeException("DEBUG");
    }

    /**
     * 函数功能：处理接受前事件。
     * 输入：
     * - 无。
     * 输出：无。
     */


    public void beforeAccept() {
        registerNext(c -> c.beforeAccept(this), context::pop);
    }

    /**
     * 函数功能：处理接受事件。
     * 输入：
     * - 无。
     * 输出：无。
     */

    public void onAccept() {
        registerNext(c -> c.onAccept(this), this::invokeNothing);
    }

    /**
     * 函数功能：处理终结符匹配事件。
     * 输入：
     * - terminal：TerminalSymbol 类型参数。
     * 输出：无。
     */


    public void onTerminal(TerminalSymbol terminal) {
        registerNext(c -> c.onTerminal(this, terminal), () -> {
            context.consumeCurrentToken();// 消费;
            context.pop();
        });
    }

    /**
     * 函数功能：处理空产生式事件。
     * 输入：
     * - head：HeadSymbol 类型参数。
     * 输出：无。
     */

    public void onEpsilonProduction(HeadSymbol head) {
        registerNext(c -> c.onEpsilonProduction(this, head), context::pop);
    }

    /**
     * 函数功能：处理产生式规约事件。
     * 输入：
     * - concatenation：GrammarConcatenation 类型参数。
     * 输出：无。
     */

    public void onProduction(GrammarConcatenation concatenation) {
        registerNext(c -> c.onProduction(this, concatenation), () -> {
            context.pop();
            Iterator<GrammarUnitSymbol> iter = concatenation.reverseIterator();
            while (iter.hasNext()) {
                GrammarUnitSymbol next = iter.next();
                context.push(next);
            }
        });
    }

    /**
     * 函数功能：注册下一个回调。
     * 输入：
     * - consumer：Consumer<PredictiveCallback> 类型参数。
     * - invoker：Runnable 类型参数。
     * 输出：无。
     */

    private void registerNext(Consumer<PredictiveCallback> consumer, Runnable invoker) {
        if (callbackIter.hasNext()) {
            consumer.accept(callbackIter.next());
        } else {
            invoker.run();
            callbackIter = register.iterator();
        }
    }

    /**
     * 函数功能：获取当前词法单元。
     * 输入：
     * - 无。
     * 输出：SourceToken 类型返回值。
     */

    public SourceToken currentToken() {
        return context.currentToken();
    }

    /**
     * 函数功能：获取起始值。
     * 输入：
     * - 无。
     * 输出：GrammarUnitSymbol 类型返回值。
     */

    public GrammarUnitSymbol getStart() {
        return context.getStart();
    }
}
