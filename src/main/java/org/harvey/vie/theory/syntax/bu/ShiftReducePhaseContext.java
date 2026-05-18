package org.harvey.vie.theory.syntax.bu;

import lombok.Data;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.SyntaxErrorMessage;
import org.harvey.vie.theory.lexical.TokenFilterPredict;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.PanicSourceTokenIterator;
import org.harvey.vie.theory.syntax.SyntaxParsingContext;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Objects;
import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 15:07
 */
@Data
public class ShiftReducePhaseContext implements SyntaxParsingContext<Integer> {
    private final ShiftReduceParsingTable table;
    private final Stack<Integer> statusStack;
    private final int startStatus;
    private PanicSourceTokenIterator iterator;
    private final ErrorContext errorContext;

    public ShiftReducePhaseContext(
            ShiftReduceParsingTable table,
            SourceTokenIterator iterator,
            TokenFilterPredict tokenFilterPredict,
            ErrorContext errorContext) {
        this.table = table;
        this.iterator = new PanicSourceTokenIterator(iterator, errorContext, tokenFilterPredict);
        this.errorContext = errorContext;
        this.statusStack = new Stack<>();
        this.startStatus = table.getStart();
        push(startStatus);
    }

    @Override
    public boolean isStackEmpty() {
        return statusStack.isEmpty();
    }

    @Override
    public Integer peek() {
        return statusStack.peek();
    }

    @Override
    public boolean validAcceptStack() {
        return statusStack.size() == 1 && statusStack.peek() == startStatus;
    }


    @Override
    public void push(Integer next) {
        statusStack.push(next);
    }

    @Override
    public Integer getStart() {
        return startStatus;
    }

    @Override
    public void pop() {
        statusStack.pop();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public void consumeCurrentToken() {
        iterator.consumeCurrentToken();
    }

    @Override
    public SourceToken currentToken() {
        return this.iterator.currentToken();
    }

    public int gotoNext(int top, HeadSymbol head) {
        return table.gotoNext(top, head);
    }
    public void addError(int offset, String message) {
        errorContext.addError(new SyntaxErrorMessage(offset,message));
    }
    public int getProductionId(SimpleGrammarProduction production) {
        Integer id = table.getProductionId(production);
        Objects.requireNonNull(id, ()-> "can not found id in table by the production: " + production);
        return id;
    }
}
