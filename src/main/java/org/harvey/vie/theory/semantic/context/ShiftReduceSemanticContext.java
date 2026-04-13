package org.harvey.vie.theory.semantic.context;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.*;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaseContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

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
public class ShiftReduceSemanticContext {
    @Setter
    private SemanticResult result;
    private final ShiftReduceCallbackRegister register;
    private final ShiftReducePhaseContext context;
    private Iterator<ShiftReduceCallback> callbackIter;

    public ShiftReduceSemanticContext(ShiftReduceCallbackRegister register,ShiftReducePhaseContext context) {
        this.register = register;
        this.context = context;
        callbackIter = register.iterator();
    }
    private void popStatus(AlterableSymbol body) {
        int k = body.isEpsilon() ? 0 : body.toConcatenation().size();
        while (k-- > 0) {
            if (context.isStackEmpty()) {
                throw new CompilerException("no more status in stack to be pop while reducing");
            }
            context.pop();
        }
    }
    private void invokeBeforeAccept(SimpleGrammarProduction production) {
        // accept 是特殊的 reduce
        AlterableSymbol body = production.getBody();
        popStatus(body);
    }

    private void invokeReduce(SimpleGrammarProduction production) {
        // 输入指针不动(归约不消耗输入符号)
        AlterableSymbol body = production.getBody();
        popStatus(body);
        int top = context.peek();
        int next = context.gotoNext(top, production.getHead());
        context.push(next);
    }

    private void invokeShift(int nextStatus) {
        context.push(nextStatus);
        context.consumeCurrentToken();
    }

    public void onStart() {
        invokeNext(c -> c.onStart(this), this::invokeNothing);
    }

    private void invokeNext(Consumer<ShiftReduceCallback> consumer, Runnable invoker) {
        if (callbackIter.hasNext()) {
            consumer.accept(callbackIter.next());
        } else {
            invoker.run();
            callbackIter = register.iterator();
        }
    }

    private void invokeNothing() {

    }

    public void onError(ShiftReduceErrorType errorType) {
        invokeNext(c -> c.onError(this, errorType), this::invokeNothing);
    }

    public void onAccept(SimpleGrammarProduction production) {
        invokeNext(c -> c.onAccept(this, production), this::invokeNothing);
    }

    public void onShift(int nextStatus, SourceToken token) {
        invokeNext(c -> c.onShift(this, nextStatus, token), () -> invokeShift(nextStatus));
    }

    public void onReduce(SimpleGrammarProduction production) {
        invokeNext(c -> c.onReduce(this, production), () -> invokeReduce(production));
    }

    public void beforeAccept(SimpleGrammarProduction production) {
        invokeNext(c -> c.beforeAccept(this, production), () -> invokeBeforeAccept(production));
    }
}
