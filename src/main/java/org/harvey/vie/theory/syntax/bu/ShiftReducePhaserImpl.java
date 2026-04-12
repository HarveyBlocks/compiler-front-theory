package org.harvey.vie.theory.syntax.bu;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.TokenFilterPredict;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.SemanticResult;
import org.harvey.vie.theory.semantic.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.callback.ShiftReduceCallbackRegister;
import org.harvey.vie.theory.semantic.callback.ShiftReduceErrorType;
import org.harvey.vie.theory.syntax.SyntaxParsingContext;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableElement;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 12:23
 */
public class ShiftReducePhaserImpl implements ShiftReducePhaser {
    private final ShiftReduceParsingTable table;

    private final ShiftReduceCallbackRegister register;
    private final TokenFilterPredict tokenFilterPredict;

    public ShiftReducePhaserImpl(
            ShiftReduceParsingTable table,
            TokenFilterPredict tokenFilterPredict,
            ShiftReduceCallbackRegister register) {
        this.tokenFilterPredict = tokenFilterPredict;
        this.table = table;
        this.register = register;
    }

    /**
     * 初始化
     * 将初始状态压入栈。
     * 输入指针 ip 指向第一个符号 a。
     * 循环执行
     * 设 s = 栈顶状态，a = 当前输入符号。
     * 查 ACTION[s, a]：
     * shift s'：
     * 将 a（可选）和 s' 压栈；
     * 输入指针后移一位。
     * reduce A → β（设 |β| = k）：
     * 从栈顶弹出 k 个状态（每个状态对应一个符号，如果栈中同时保存了符号则也弹出符号）；
     * 弹出后，栈顶状态变为 s_t；
     * 查 GOTO[s_t, A] 得到 s_new；
     * 将 A（可选）和 s_new 压栈；
     * 注意：输入指针不动（归约不消耗输入符号）。
     * accept：
     * 结束，输入串合法。
     * error：
     * 调用错误恢复（或直接报错）。
     * 结束条件：
     * 遇到 accept 时成功
     * 若在无 accept 的情况下栈空且输入未结束则失败。
     */
    @Override
    public SemanticResult phase(SourceTokenIterator iterator, ErrorContext errorContext) {
        ShiftReducePhaseContext ctx = new ShiftReducePhaseContext(
                table,
                iterator,
                tokenFilterPredict,
                errorContext
        );
        ShiftReduceSemanticContext context = new ShiftReduceSemanticContext(
                register, ctx
        );
        context.onStart();
        while (true) {
            SourceToken current = ctx.currentToken();
            if (ctx.isStackEmpty()) {
                context.onError(ShiftReduceErrorType.STACK_UNDERFLOW);
                break;
            }
            int top = ctx.peek();
            ActiveTableElement element = table.activeNext(top, table.matchTerminal(current));
            if (element == null) {
                // error
                context.onError(ShiftReduceErrorType.UNDEFINED_ACTION);
            } else if (element.isShift()) {
                onShift(context, element, current);
            } else if (element.isReduce()) {
                onReduce(context, element);
                if (element.isAccept()) {
                    break; // 结束
                }
            } else {
                throw new CompilerException(new IllegalStateException("Unknown active table element type."));
            }
        }
        return context.getResult();
    }

    private void onReduce(ShiftReduceSemanticContext context, ActiveTableElement element) {
        SimpleGrammarProduction production = table.getProduction(element.getProduction());
        if (element.isAccept()) {
            context.beforeAccept(production);
            onAccept(context, production);
        } else {
            context.onReduce(production);
        }
    }

    private void onAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        SyntaxParsingContext<Integer> syntaxContext = context.getContext();
        if (!syntaxContext.hasNext()) {
            if (syntaxContext.validAcceptStack()) {
                context.onAccept(production);
            } else {
                context.onError(ShiftReduceErrorType.INVALID_ACCEPTING_STATE);
            }
        } else {
            context.onError(ShiftReduceErrorType.TRAILING_INPUT_AFTER_ACCEPT);
        }
    }

    private void onShift(ShiftReduceSemanticContext context, ActiveTableElement element, SourceToken token) {
        context.onShift(element.nextStatus(), token);
    }
}
