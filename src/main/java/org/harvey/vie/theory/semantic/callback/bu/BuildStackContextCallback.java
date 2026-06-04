package org.harvey.vie.theory.semantic.callback.bu;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;

import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 16:25
 */
public class BuildStackContextCallback<T> implements ShiftReduceCallback {
    private final Supplier<T> supplier;

    /**
     * 函数功能：创建 BuildStackContextCallback 对象。
     * 输入：
     * - supplier：Supplier<T> 类型参数。
     * 输出：无。
     */

    public BuildStackContextCallback(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * 函数功能：处理接受前事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */

    @Override
    public void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        reduceProduction(context, production);
        ShiftReduceCallback.super.beforeAccept(context, production);
    }

    /**
     * 函数功能：处理规约事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */

    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        T item = reduceProduction(context, production);
        Stack<T> stackContext = supplier.getStackContext(context);
        stackContext.push(item);
        ShiftReduceCallback.super.onReduce(context, production);
    }

    /**
     * 函数功能：获取规约产生式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：T 类型返回值。
     */

    private T reduceProduction(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        Stack<T> stackContext = supplier.getStackContext(context);
        T[] children = popContext(context, stackContext, production.getBody());
        return supplier.instanceNodeOnReduce(context, production, children);
    }

    /**
     * 函数功能：弹出语义栈上下文。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - stackContext：Stack<T> 类型参数。
     * - body：AlterableSymbol 类型参数。
     * 输出：T[] 类型数组。
     */

    private T[] popContext(ShiftReduceSemanticContext context, Stack<T> stackContext, AlterableSymbol body) {
        int k = body.isEpsilon() ? 0 : body.toConcatenation().size();
        T[] children = supplier.instanceChildrenArray(k);
        while (k-- > 0) {
            if (stackContext.isEmpty()) {
                throw new CompilerException("no more status in stack to be pop while reducing");
            }
            children[k] = stackContext.pop();
        }
        return children;
    }

    /**
     * 函数功能：处理移进事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - nextStatus：int 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：无。
     */

    @Override
    public void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token) {
        Stack<T> stackContext = supplier.getStackContext(context);
        T item = supplier.instanceNodeOnShift(context, token);
        stackContext.push(item);
        ShiftReduceCallback.super.onShift(context, nextStatus, token);
    }

    public interface Supplier<T> {
        /**
         * 函数功能：获取语义栈上下文。
         * 输入：
         * - context：ShiftReduceSemanticContext 类型参数。
         * 输出：Stack<T> 类型返回值。
         */
        Stack<T> getStackContext(ShiftReduceSemanticContext context);

        /**
         * 函数功能：创建子节点数组。
         * 输入：
         * - n：int 类型参数。
         * 输出：T[] 类型数组。
         */

        T[] instanceChildrenArray(int n);

        T instanceNodeOnReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production, T[] children);

        T instanceNodeOnShift(ShiftReduceSemanticContext context, SourceToken token);
    }
}
