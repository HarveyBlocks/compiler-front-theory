package org.harvey.vie.theory.syntax.bu;

import lombok.Data;
import lombok.Setter;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.SyntaxErrorMessage;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.SemanticResult;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.callback.ShiftReduceCallback;
import org.harvey.vie.theory.syntax.callback.ShiftReduceCallbackRegister;
import org.harvey.vie.theory.syntax.callback.ShiftReduceErrorType;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;

import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 15:07
 */
@Data
public class ShiftReducePhaseContext {
    private final ShiftReduceParsingTable table;
    private final Stack<Integer> statusStack;
    private final int startStatus;
    private final ShiftReduceCallbackRegister register;
    private Iterator<ShiftReduceCallback> callbackIter;
    private SourceTokenIterator iterator;
    private final ErrorContext errorContext;
    @Setter
    private SemanticResult result;

    public ShiftReducePhaseContext(
            ShiftReduceParsingTable table,
            SourceTokenIterator iterator,
            ErrorContext errorContext,
            ShiftReduceCallbackRegister register) {
        this.table = table;
        this.iterator = iterator;
        this.errorContext = errorContext;
        this.register = register;
        this.statusStack = new Stack<>();
        this.startStatus = table.getStart();
        pushStatus(startStatus);
        this.callbackIter = register.iterator();
    }


    public boolean isStackEmpty() {
        return statusStack.isEmpty();
    }

    public int peekStatus() {
        return statusStack.peek();
    }

    public boolean validAcceptStack() {
        return statusStack.size() == 1 && statusStack.peek() == startStatus;
    }

    private void pushStatus(int next) {
        statusStack.push(next);
    }

    private void popStatus(AlterableSymbol body) {
        int k = body.isEpsilon() ? 0 : body.toConcatenation().size();
        while (k-- > 0) {
            if (statusStack.isEmpty()) {
                throw new CompilerException("no more status in stack to be pop while reducing");
            }
            statusStack.pop();
        }
    }

    public boolean hasNextToken() {
        return iterator.hasNext();
    }

    public SourceToken currentToken() {
        if (!iterator.hasNext()) {
            return SourceTokenIterator.NO_MORE_TOKEN;
        }
        while (true) {
            try {
                return iterator.current();
            } catch (CompileException e) {
                // 未完成的token
                errorContext.addError(new SyntaxErrorMessage(iterator.getOffset(), e.getMessage()));
                // 跳过(panic模式)
            }
        }
    }


    private void consumerCurrentToken() {
        // 消费
        try {
            iterator.next();
        } catch (CompileException e) {
            throw new CompilerException(
                    "current mechanism fails. If current must be executed before this next, then this next must not fail!  ",
                    e
            );
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
        int top = peekStatus();
        int next = table.gotoNext(top, production.getHead());
        pushStatus(next);
    }

    private void invokeShift(int nextStatus) {
        pushStatus(nextStatus);
        consumerCurrentToken();
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
