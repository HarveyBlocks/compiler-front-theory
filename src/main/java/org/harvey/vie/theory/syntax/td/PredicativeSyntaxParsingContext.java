package org.harvey.vie.theory.syntax.td;

import lombok.Getter;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.lexical.TokenFilterPredict;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.PanicSourceTokenIterator;
import org.harvey.vie.theory.syntax.SyntaxParsingContext;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;

import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 13:13
 */
@Getter
public class PredicativeSyntaxParsingContext implements SyntaxParsingContext<GrammarUnitSymbol> {
    public static final TerminalSymbol END_MARK = PredictiveParsingTable.END_MARK_SYMBOL;
    private final Stack<GrammarUnitSymbol> symbolStack;
    private final PanicSourceTokenIterator iterator;
    private final ErrorContext errorContext;
    private final GrammarUnitSymbol start;
    public PredicativeSyntaxParsingContext(
            GrammarUnitSymbol start,
            SourceTokenIterator iterator,
            ErrorContext errorContext,
            TokenFilterPredict tokenFilterPredict) {
        this.iterator = new PanicSourceTokenIterator(iterator, errorContext, tokenFilterPredict);
        this.errorContext = errorContext;
        // --symbol--
        this.symbolStack = new Stack<>();
        symbolStack.push(END_MARK);
        symbolStack.push(start);
        this.start = start;
    }


    @Override
    public SourceToken currentToken() {
        return iterator.currentToken();

    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public boolean isStackEmpty() {
        return symbolStack.isEmpty();
    }

    @Override
    public GrammarUnitSymbol peek() {
        return symbolStack.peek();
    }

    @Override
    public boolean validAcceptStack() {
        return symbolStack.size() == 1 && symbolStack.peek() == END_MARK;
    }

    @Override
    public void pop() {
        symbolStack.pop();
    }

    @Override
    public void consumeCurrentToken() {
        iterator.consumeCurrentToken();
    }


    @Override
    public void push(GrammarUnitSymbol next) {
        symbolStack.push(next);
    }


}
