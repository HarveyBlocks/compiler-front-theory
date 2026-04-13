package org.harvey.vie.theory.semantic.context;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.PredicativeErrorType;
import org.harvey.vie.theory.semantic.callback.PredictiveCallback;
import org.harvey.vie.theory.semantic.callback.PredictiveCallbackRegister;
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
    @Setter
    private SemanticResult result;
    private final PredictiveCallbackRegister register;
    private final SyntaxParsingContext<GrammarUnitSymbol> context;
    private Iterator<PredictiveCallback> callbackIter;

    public PredictiveSemanticContext(PredictiveCallbackRegister register, SyntaxParsingContext<GrammarUnitSymbol> context) {
        this.register = register;
        this.context = context;
        callbackIter = register.iterator();
    }

    private void invokeNothing() {
    }

    public void onStart() {
        registerNext(c -> c.onStart(this), this::invokeNothing);
    }

    public void onError(PredicativeErrorType predicativeErrorType) {
        registerNext(c -> c.onError(this, predicativeErrorType), this::invokeNothing);
        throw new RuntimeException("DEBUG");
    }


    public void beforeAccept() {
        registerNext(c -> c.beforeAccept(this), context::pop);
    }

    public void onAccept() {
        registerNext(c -> c.onAccept(this), this::invokeNothing);
    }


    public void onTerminal(TerminalSymbol terminal) {
        registerNext(c -> c.onTerminal(this, terminal), () -> {
            context.consumeCurrentToken();// 消费;
            context.pop();
        });
    }

    public void onEpsilonProduction(HeadSymbol head) {
        registerNext(c -> c.onEpsilonProduction(this, head), context::pop);
    }

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

    private void registerNext(Consumer<PredictiveCallback> consumer, Runnable invoker) {
        if (callbackIter.hasNext()) {
            consumer.accept(callbackIter.next());
        } else {
            invoker.run();
            callbackIter = register.iterator();
        }
    }

    public SourceToken currentToken() {
        return context.currentToken();
    }

    public GrammarUnitSymbol getStart() {
        return context.getStart();
    }
}
