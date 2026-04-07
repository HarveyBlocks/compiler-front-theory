package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.callback.PredicativeErrorType;
import org.harvey.vie.theory.syntax.callback.PredictiveCallback;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;

import java.util.Iterator;

/**
 * TODO null for end mark
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:17
 */
public class PredictivePhaserImpl implements PredictivePhaser {

    private final PredictiveParsingTable predictiveParsingTable;

    private final GrammarUnitSymbol start;

    private final PredictiveCallback callback;

    public PredictivePhaserImpl(
            GrammarUnitSymbol start, PredictiveParsingTable predictiveParsingTable, PredictiveCallback callback) {
        this.start = start;
        this.predictiveParsingTable = predictiveParsingTable;
        this.callback = callback;
    }

    @Override
    public void phase(SourceTokenIterator iterator, ErrorContext errorContext) {
        SyntaxParsingContext ctx = new SyntaxParsingContext(start, iterator, errorContext);
        callback.onStart(ctx);
        while (true) {
            if (ctx.isStackEmpty()) {
                callback.onError(ctx, PredicativeErrorType.STACK_UNDERFLOW);
            }
            GrammarUnitSymbol top = ctx.peekSymbol();
            if (top == SyntaxParsingContext.END_MARK) {
                // 1. 当 X=a=$ 停止分析, 接受, 成功
                if (!iterator.hasNext()) {
                    callback.beforeAccept(ctx);
                    if (ctx.isStackEmpty()) {
                        // 接受, 成功
                        callback.onAccept(ctx);
                    } else {
                        callback.onError(ctx, PredicativeErrorType.INVALID_ACCEPTING_STATE);
                    }
                } else {
                    callback.onError(ctx, PredicativeErrorType.TRAILING_INPUT_AFTER_ACCEPT);
                }
                break;
            }
            SourceToken token = ctx.currentToken();
            if (top.isTerminal()) {
                terminal(token, top.toTerminal(), ctx);
            } else {
                head(token, top.toHead(), ctx);
                // 3.4 goto 1
            }
        }
    }

    private void terminal(SourceToken token, TerminalSymbol terminal, SyntaxParsingContext ctx) {
        // 2. 当 X is terminal 且 X = a != $, 弹出 X, 前进输入, goto 1
        if (terminal.match(token)) {
            callback.onTerminal(ctx, terminal);
        } else {
            // 不匹配
            callback.onError(ctx, PredicativeErrorType.TERMINAL_CONFLICT);
        }
    }

    private void head(SourceToken token, HeadSymbol head, SyntaxParsingContext ctx) {
        // 不是 terminal
        // 3. 当 X not terminal, 查表 M[X,a]
        AlterableSymbol alterableSymbol = predictiveParsingTable.get(head, token);
        if (alterableSymbol == null) {
            // 冲突
            callback.onError(ctx, PredicativeErrorType.UNDEFINED_PRODUCTION);
        }
        // 3.2 逆序入栈
        else if (alterableSymbol == GrammarSymbol.EPSILON) {
            callback.onEpsilonProduction(ctx, head);
        } else if (alterableSymbol.isConcatenation()) {
            // 表项产生 X -> UVW
            GrammarConcatenation concatenation = alterableSymbol.toConcatenation();
            callback.onProduction(ctx, concatenation);
        } else {
            throw new CompilerException(new IllegalStateException("Grammar definition is wrong, unreasonable type."));
        }
        // 3.3 不消费输入
    }

}
