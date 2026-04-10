package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.TokenFilterPredict;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.SemanticResult;
import org.harvey.vie.theory.syntax.callback.PredicativeErrorType;
import org.harvey.vie.theory.syntax.callback.PredictiveCallbackRegister;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;

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
    private final PredictiveCallbackRegister register;
    private final TokenFilterPredict tokenFilterPredict;

    public PredictivePhaserImpl(
            GrammarUnitSymbol start,
            PredictiveParsingTable predictiveParsingTable,
            PredictiveCallbackRegister register,
            TokenFilterPredict tokenFilterPredict) {
        this.start = start;
        this.predictiveParsingTable = predictiveParsingTable;
        this.register = register;

        this.tokenFilterPredict = tokenFilterPredict;
    }

    @Override
    public SemanticResult phase(SourceTokenIterator iterator, ErrorContext errorContext) {
        SyntaxParsingContext ctx = new SyntaxParsingContext(
                start,
                iterator,
                errorContext,
                tokenFilterPredict,
                register
        );
        ctx.onStart();
        while (true) {
            if (ctx.isStackEmpty()) {
                ctx.onError(PredicativeErrorType.STACK_UNDERFLOW);
            }
            GrammarUnitSymbol top = ctx.peekSymbol();
            if (top == SyntaxParsingContext.END_MARK) {
                // 1. 当 X=a=$ 停止分析, 接受, 成功
                if (!ctx.hasNext()) {
                    ctx.beforeAccept();
                    if (ctx.isStackEmpty()) {
                        // 接受, 成功
                        ctx.onAccept();
                    } else {
                        ctx.onError(PredicativeErrorType.INVALID_ACCEPTING_STATE);
                    }
                } else {
                    ctx.onError(PredicativeErrorType.TRAILING_INPUT_AFTER_ACCEPT);
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
        return ctx.getResult();
    }

    private void terminal(SourceToken token, TerminalSymbol terminal, SyntaxParsingContext ctx) {
        // 2. 当 X is terminal 且 X = a != $, 弹出 X, 前进输入, goto 1
        if (terminal.match(token)) {
            ctx.onTerminal(terminal);
        } else {
            // 不匹配
            ctx.onError(PredicativeErrorType.TERMINAL_CONFLICT);
        }
    }

    private void head(SourceToken token, HeadSymbol head, SyntaxParsingContext ctx) {
        // 不是 terminal
        // 3. 当 X not terminal, 查表 M[X,a]
        AlterableSymbol alterableSymbol = predictiveParsingTable.get(head, token);
        if (alterableSymbol == null) {
            // 冲突
            ctx.onError(PredicativeErrorType.UNDEFINED_PRODUCTION);
        }
        // 3.2 逆序入栈
        else if (alterableSymbol == GrammarSymbol.EPSILON) {
            ctx.onEpsilonProduction(head);
        } else if (alterableSymbol.isConcatenation()) {
            // 表项产生 X -> UVW
            GrammarConcatenation concatenation = alterableSymbol.toConcatenation();
            ctx.onProduction(concatenation);
        } else {
            throw new CompilerException(new IllegalStateException("Grammar definition is wrong, unreasonable type."));
        }
        // 3.3 不消费输入
    }

}
