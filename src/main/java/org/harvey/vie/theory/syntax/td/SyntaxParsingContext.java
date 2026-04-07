package org.harvey.vie.theory.syntax.td;

import lombok.Getter;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.SyntaxErrorMessage;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;

import java.util.Iterator;
import java.util.Stack;

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

    public SyntaxParsingContext(GrammarUnitSymbol start, SourceTokenIterator iterator, ErrorContext errorContext) {
        this.iterator = iterator;
        this.errorContext = errorContext;
        // --symbol--
        this.symbolStack = new Stack<>();
        symbolStack.push(END_MARK);
        symbolStack.push(start);
        this.start = start;
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

    public void invokeBeforeAccept() {
        popSymbol();
    }

    public SourceToken invokeTerminal() {
        SourceToken consumed = next();// 消费
        popSymbol();
        return consumed;
    }

    public void invokeEpsilonProduction(HeadSymbol head) {
        popSymbol();
    }

    public void invokeProduction(GrammarConcatenation concatenation) {
        popSymbol();
        Iterator<GrammarUnitSymbol> iter = concatenation.reverseIterator();
        while (iter.hasNext()) {
            GrammarUnitSymbol next = iter.next();
            pushSymbol(next);
        }
    }
}
