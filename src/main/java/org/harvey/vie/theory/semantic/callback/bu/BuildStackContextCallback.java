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
    private final Visitor<T> visitor;

    public BuildStackContextCallback(Supplier<T> supplier, Visitor<T> visitor) {
        this.supplier = supplier;
        this.visitor = visitor;
    }

    @Override
    public void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        popContext(context, supplier.getStackContext(context), production.getBody());
        ShiftReduceCallback.super.beforeAccept(context, production);
    }

    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        Stack<T> stackContext = supplier.getStackContext(context);
        T[] children = popContext(context, stackContext, production.getBody());
        T item = supplier.instanceNodeOnReduce(context, production, children);
        visitor.onReducePush(context, stackContext, item);
        ShiftReduceCallback.super.onReduce(context, production);
    }

    private T[] popContext(ShiftReduceSemanticContext context, Stack<T> stackContext, AlterableSymbol body) {
        int k = body.isEpsilon() ? 0 : body.toConcatenation().size();
        T[] children = supplier.instanceChildrenArray(k);
        while (k-- > 0) {
            if (stackContext.isEmpty()) {
                throw new CompilerException("no more status in stack to be pop while reducing");
            }
            children[k] = visitor.onPop(context, stackContext);
        }
        return children;
    }

    @Override
    public void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token) {
        Stack<T> stackContext = supplier.getStackContext(context);
        T item = supplier.instanceNodeOnShift(context, token);
        visitor.onShiftPush(context, stackContext, item);
        ShiftReduceCallback.super.onShift(context, nextStatus, token);
    }

    public interface Supplier<T> {
        Stack<T> getStackContext(ShiftReduceSemanticContext context);

        T[] instanceChildrenArray(int n);

        T instanceNodeOnReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production, T[] children);

        T instanceNodeOnShift(ShiftReduceSemanticContext context, SourceToken token);
    }

    public interface Visitor<T> {
        default void onReducePush(ShiftReduceSemanticContext context, Stack<T> stack, T item) {
            stack.push(item);
        }

        default T onPop(ShiftReduceSemanticContext context, Stack<T> stack) {
            return stack.pop();
        }

        default void onShiftPush(ShiftReduceSemanticContext context, Stack<T> stack, T item) {
            stack.push(item);
        }
    }
}
