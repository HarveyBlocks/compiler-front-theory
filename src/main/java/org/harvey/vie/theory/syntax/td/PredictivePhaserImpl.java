package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.TokenFilterPredict;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.callback.td.PredicativeErrorType;
import org.harvey.vie.theory.semantic.callback.td.PredictiveCallbackRegister;
import org.harvey.vie.theory.semantic.context.PredictiveSemanticContext;
import org.harvey.vie.theory.semantic.context.SemanticResult;
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

    /**
     * 函数功能：创建 PredictivePhaserImpl 对象。
     * 输入：
     * - start：GrammarUnitSymbol 类型参数。
     * - predictiveParsingTable：PredictiveParsingTable 类型参数。
     * - register：PredictiveCallbackRegister 类型参数。
     * - tokenFilterPredict：TokenFilterPredict 类型参数。
     * 输出：无。
     */

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

    /**
     * 函数功能：执行语法分析并返回语义结果。
     * 输入：
     * - iterator：SourceTokenIterator 类型参数。
     * - errorContext：ErrorContext 类型参数。
     * 输出：SemanticResult 类型返回值。
     */

    @Override
    public SemanticResult phase(SourceTokenIterator iterator, ErrorContext errorContext) {
        PredicativeSyntaxParsingContext context = new PredicativeSyntaxParsingContext(
                start,
                iterator,
                errorContext,
                tokenFilterPredict
        );
        PredictiveSemanticContext ctx = new PredictiveSemanticContext(
                register,
                context
        );
        ctx.onStart();
        while (true) {
            if (context.isStackEmpty()) {
                ctx.onError(PredicativeErrorType.STACK_UNDERFLOW);
            }
            GrammarUnitSymbol top = context.peek();
            if (top == PredicativeSyntaxParsingContext.END_MARK) {
                // 1. 当 X=a=$ 停止分析, 接受, 成功
                if (!context.hasNext()) {
                    ctx.beforeAccept();
                    if (context.isStackEmpty()) {
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
            SourceToken token = context.currentToken();
            if (top.isTerminal()) {
                terminal(token, top.toTerminal(), ctx);
            } else {
                head(token, top.toHead(), ctx);
                // 3.4 goto 1
            }
        }
        return ctx.getResult();
    }

    /**
     * 函数功能：处理终结符匹配。
     * 输入：
     * - token：SourceToken 类型参数。
     * - terminal：TerminalSymbol 类型参数。
     * - ctx：PredictiveSemanticContext 类型参数。
     * 输出：无。
     */

    private void terminal(SourceToken token, TerminalSymbol terminal, PredictiveSemanticContext ctx) {
        // 2. 当 X is terminal 且 X = a != $, 弹出 X, 前进输入, goto 1
        if (terminal.match(token)) {
            ctx.onTerminal(terminal);
        } else {
            // 不匹配
            ctx.onError(PredicativeErrorType.TERMINAL_CONFLICT);
        }
    }

    /**
     * 函数功能：处理非终结符展开。
     * 输入：
     * - token：SourceToken 类型参数。
     * - head：HeadSymbol 类型参数。
     * - ctx：PredictiveSemanticContext 类型参数。
     * 输出：无。
     */

    private void head(SourceToken token, HeadSymbol head, PredictiveSemanticContext ctx) {
        // 不是 terminal
        // 3. 当 X not terminal, 查表 M[X,a]
        AlterableSymbol alterableSymbol = predictiveParsingTable.get(head, token);
        if (alterableSymbol == null) {
            // 冲突
            ctx.onError(PredicativeErrorType.UNDEFINED_PRODUCTION);
        }
        // 3.2 逆序入栈
        else if (alterableSymbol.isEpsilon()) {
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
