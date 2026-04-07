package org.harvey.vie.theory.syntax.bu;

import lombok.Data;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.SyntaxErrorMessage;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;

import java.util.Stack;

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
    private SourceTokenIterator iterator;
    private final ErrorContext errorContext;

    public ShiftReducePhaseContext(
            ShiftReduceParsingTable table,
            SourceTokenIterator iterator,
            ErrorContext errorContext) {
        this.table = table;
        this.iterator = iterator;
        this.errorContext = errorContext;
        this.statusStack = new Stack<>();
        this.startStatus = table.getStart();
        pushStatus(startStatus);
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


    private SourceToken consumerCurrentToken() {
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

    public void invokeBeforeAccept(SimpleGrammarProduction production) {
        // accept 是特殊的 reduce
        AlterableSymbol body = production.getBody();
        popStatus(body);
    }

    public void invokeReduce(SimpleGrammarProduction production) {
        // 输入指针不动(归约不消耗输入符号)
        AlterableSymbol body = production.getBody();
        popStatus(body);
        int top = peekStatus();
        int next = table.gotoNext(top, production.getHead());
        pushStatus(next);
    }

    public SourceToken invokeShift(int nextStatus) {
        pushStatus(nextStatus);
        return consumerCurrentToken();
    }
}
