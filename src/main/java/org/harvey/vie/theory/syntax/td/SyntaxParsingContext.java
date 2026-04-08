package org.harvey.vie.theory.syntax.td;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.SyntaxErrorMessage;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.SemanticResult;
import org.harvey.vie.theory.syntax.callback.PredicativeErrorType;
import org.harvey.vie.theory.syntax.callback.PredictiveCallback;
import org.harvey.vie.theory.syntax.callback.PredictiveCallbackRegister;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;

import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 13:13
 */
@Getter
public class SyntaxParsingContext {
    public static final TerminalSymbol END_MARK = PredictiveParsingTable.END_MARK_SYMBOL;
    private final Stack<GrammarUnitSymbol> symbolStack;
    private final SourceTokenIterator iterator;
    private final ErrorContext errorContext;
    private final GrammarUnitSymbol start;
    private final PredictiveCallbackRegister register;
    private Iterator<PredictiveCallback> callbackIter;
    @Setter
    private SemanticResult result;

    public SyntaxParsingContext(
            GrammarUnitSymbol start,
            SourceTokenIterator iterator,
            ErrorContext errorContext,
            PredictiveCallbackRegister register) {
        this.iterator = iterator;
        this.errorContext = errorContext;
        // --symbol--
        this.symbolStack = new Stack<>();
        symbolStack.push(END_MARK);
        symbolStack.push(start);
        this.start = start;
        this.register = register;
        this.callbackIter = register.iterator();
    }

    public SourceToken currentToken() {
        SourceToken token;
        while (true) {
            try {
                token = iterator.current();
                break;
            } catch (CompileException e) {
                // 未完成的token
                errorContext.addError(new SyntaxErrorMessage(iterator.getOffset(), e.getMessage()));
                // 跳过
            }
        }
        return token;
    }

    private SourceToken next() {
        // 消费
        try {
            return iterator.next();
        } catch (CompileException e) {
            throw new CompilerException(
                    "current mechanism fails. If current must be executed before this next, then this next must not fail!  ",
                    e
            );
        }
    }

    public boolean isStackEmpty() {
        return symbolStack.isEmpty();
    }

    private void popSymbol() {
        symbolStack.pop();
    }

    private void pushSymbol(GrammarUnitSymbol next) {
        symbolStack.push(next);
    }

    public GrammarUnitSymbol peekSymbol() {
        return symbolStack.peek();
    }

    private void invokeBeforeAccept() {
        this.popSymbol();
    }


    private void invokeTerminal() {
        SourceToken consumed = next();// 消费
        popSymbol();
    }

    private void invokeEpsilonProduction(HeadSymbol head) {
        popSymbol();
    }

    private void invokeProduction(GrammarConcatenation concatenation) {
        popSymbol();
        Iterator<GrammarUnitSymbol> iter = concatenation.reverseIterator();
        while (iter.hasNext()) {
            GrammarUnitSymbol next = iter.next();
            pushSymbol(next);
        }
    }

    private void invokeNothing() {

    }

    public void onStart() {
        registerNext(c -> c.onStart(this), this::invokeNothing);
    }

    public void onError(PredicativeErrorType predicativeErrorType) {
        registerNext(c -> c.onError(this, predicativeErrorType), this::invokeNothing);
    }


    public void beforeAccept() {
        registerNext(c -> c.beforeAccept(this), this::invokeBeforeAccept);
    }

    public void onAccept() {
        registerNext(c -> c.onAccept(this), this::invokeNothing);
    }


    public void onTerminal(TerminalSymbol terminal) {
        registerNext(c -> c.onTerminal(this, terminal), this::invokeTerminal);
    }

    public void onEpsilonProduction(HeadSymbol head) {
        registerNext(c -> c.onEpsilonProduction(this, head), () -> invokeEpsilonProduction(head));
    }

    public void onProduction(GrammarConcatenation concatenation) {
        registerNext(c -> c.onProduction(this, concatenation), () -> invokeProduction(concatenation));
    }

    private void registerNext(Consumer<PredictiveCallback> consumer, Runnable invoker) {
        if (callbackIter.hasNext()) {
            consumer.accept(callbackIter.next());
        } else {
            invoker.run();
            callbackIter = register.iterator();
        }
    }
}
