package org.harvey.vie.theory.syntax.bu;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.bu.table.element.ActiveTableElement;
import org.harvey.vie.theory.syntax.callback.ShiftReduceErrorType;
import org.harvey.vie.theory.syntax.callback.ShiftReduceSemanticCallback;
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
    private final ShiftReduceSemanticCallback callback;

    public ShiftReducePhaserImpl(
            ShiftReduceParsingTable table, ShiftReduceSemanticCallback callback) {
        this.table = table;
        this.callback = callback;
    }

    @Override
    public void phase(SourceTokenIterator iterator, ErrorContext errorContext) {
        // 初始化
        //  将初始状态压入栈。
        //  输入指针 ip 指向第一个符号 a。
        // 循环执行
        //  设 s = 栈顶状态，a = 当前输入符号。
        //  查 ACTION[s, a]：
        //      shift s'：
        //          将 a（可选）和 s' 压栈；
        //          输入指针后移一位。
        //      reduce A → β（设 |β| = k）：
        //          从栈顶弹出 k 个状态（每个状态对应一个符号，如果栈中同时保存了符号则也弹出符号）；
        //          弹出后，栈顶状态变为 s_t；
        //          查 GOTO[s_t, A] 得到 s_new；
        //          将 A（可选）和 s_new 压栈；
        //          注意：输入指针不动（归约不消耗输入符号）。
        //      accept：
        //          结束，输入串合法。
        //      error：
        //          调用错误恢复（或直接报错）。
        // 结束条件：
        //  遇到 accept 时成功
        //  若在无 accept 的情况下栈空且输入未结束则失败。
        ShiftReducePhaseContext context = new ShiftReducePhaseContext(table, iterator, errorContext);
        callback.onStart(context);
        while (true) {
            SourceToken current = context.currentToken();
            if (context.isStackEmpty()) {
                callback.onError(context, ShiftReduceErrorType.STACK_UNDERFLOW);
                break;
            }
            int top = context.peekStatus();
            ActiveTableElement element = table.activeNext(top, table.matchTerminal(current));
            if (element == null) {
                // error
                callback.onError(context, ShiftReduceErrorType.UNDEFINED_ACTION);
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
    }

    private void onReduce(ShiftReducePhaseContext context, ActiveTableElement element) {
        SimpleGrammarProduction production = table.getProduction(element.getProduction());
        if (element.isAccept()) {
            callback.beforeAccept(context, production);
            onAccept(context, production);
        } else {
            callback.onReduce(context, production);
        }
    }

    private void onAccept(ShiftReducePhaseContext context, SimpleGrammarProduction production) {

        if (!context.hasNextToken()) {
            if (context.validAcceptStack()) {
                callback.onAccept(context, production);
            } else {
                callback.onError(context, ShiftReduceErrorType.INVALID_ACCEPTING_STATE);
            }
        } else {
            callback.onError(context, ShiftReduceErrorType.TRAILING_INPUT_AFTER_ACCEPT);
        }
    }

    private void onShift(ShiftReducePhaseContext context, ActiveTableElement element, SourceToken token) {
        callback.onShift(context, element.nextStatus(), token);
    }
}
