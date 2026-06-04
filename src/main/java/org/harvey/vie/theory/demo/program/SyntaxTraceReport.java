package org.harvey.vie.theory.demo.program;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceErrorType;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.ArrayList;
import java.util.List;

/**
 * Records stage-2 shift/reduce behavior for per-case reports.
 */
public class SyntaxTraceReport implements ShiftReduceCallback {
    private final List<TraceEntry> entries = new ArrayList<>();
    private ShiftReduceErrorType errorType;

    /**
     * 函数功能：记录移进动作的语法分析追踪条目。
     * 输入：
     * - context：当前移进归约语义上下文。
     * - nextStatus：移进后的目标状态编号。
     * - token：被移进的源词法单元。
     * 输出：无。
     */
    @Override
    public void onShift(ShiftReduceSemanticContext context, int nextStatus, SourceToken token) {
        entries.add(new TraceEntry(
                "SHIFT",
                token == null ? "<null>" : token.getType().hint(),
                token == null ? "" : SourceTokenStringMapping.utf8(token),
                token == null ? -1 : token.getOffset(),
                "shift " + nextStatus,
                context.getSyntaxContext().statusStackString()
        ));
        ShiftReduceCallback.super.onShift(context, nextStatus, token);
    }

    /**
     * 函数功能：记录归约动作的语法分析追踪条目。
     * 输入：
     * - context：当前移进归约语义上下文。
     * - production：用于归约的简单文法产生式。
     * 输出：无。
     */
    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        entries.add(new TraceEntry(
                "REDUCE",
                "",
                "",
                -1,
                production.toString(),
                context.getSyntaxContext().statusStackString()
        ));
        ShiftReduceCallback.super.onReduce(context, production);
    }

    /**
     * 函数功能：记录接受前准备动作的语法分析追踪条目。
     * 输入：
     * - context：当前移进归约语义上下文。
     * - production：接受阶段使用的简单文法产生式。
     * 输出：无。
     */
    @Override
    public void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        entries.add(new TraceEntry(
                "ACCEPT-PREPARE",
                "",
                "",
                -1,
                production.toString(),
                context.getSyntaxContext().statusStackString()
        ));
        ShiftReduceCallback.super.beforeAccept(context, production);
    }

    /**
     * 函数功能：记录接受动作的语法分析追踪条目。
     * 输入：
     * - context：当前移进归约语义上下文。
     * - production：接受阶段使用的简单文法产生式。
     * 输出：无。
     */
    @Override
    public void onAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        entries.add(new TraceEntry(
                "ACCEPT",
                "",
                "",
                -1,
                production.toString(),
                context.getSyntaxContext().statusStackString()
        ));
        ShiftReduceCallback.super.onAccept(context, production);
    }

    /**
     * 函数功能：记录语法分析错误类型和错误追踪条目。
     * 输入：
     * - context：当前移进归约语义上下文。
     * - errorType：移进归约错误类型。
     * 输出：无。
     */
    @Override
    public void onError(ShiftReduceSemanticContext context, ShiftReduceErrorType errorType) {
        this.errorType = errorType;
        entries.add(new TraceEntry(
                "ERROR",
                "",
                "",
                -1,
                errorType.name(),
                context.getSyntaxContext().statusStackString()
        ));
        ShiftReduceCallback.super.onError(context, errorType);
    }

    /**
     * 函数功能：获取当前追踪条目的不可变快照。
     * 输入：
     * - 无。
     * 输出：TraceEntry 列表快照。
     */
    public List<TraceEntry> snapshot() {
        return List.copyOf(entries);
    }

    /**
     * 函数功能：获取记录到的语法分析错误类型。
     * 输入：
     * - 无。
     * 输出：ShiftReduceErrorType 错误类型；无错误时为 null。
     */
    public ShiftReduceErrorType getErrorType() {
        return errorType;
    }

    public static final class TraceEntry {
        private final String phase;
        private final String tokenType;
        private final String lexeme;
        private final int offset;
        private final String action;
        private final String stackBefore;

        /**
         * 函数功能：创建语法分析追踪条目。
         * 输入：
         * - phase：分析阶段名称。
         * - tokenType：当前词法单元类型名称。
         * - lexeme：当前词法单元文本。
         * - offset：当前词法单元偏移量。
         * - action：分析动作描述。
         * - stackBefore：动作执行前的状态栈文本。
         * 输出：无。
         */
        public TraceEntry(
                String phase,
                String tokenType,
                String lexeme,
                int offset,
                String action,
                String stackBefore) {
            this.phase = phase;
            this.tokenType = tokenType;
            this.lexeme = lexeme;
            this.offset = offset;
            this.action = action;
            this.stackBefore = stackBefore;
        }

        /**
         * 函数功能：获取分析阶段名称。
         * 输入：
         * - 无。
         * 输出：分析阶段字符串。
         */
        public String getPhase() {
            return phase;
        }

        /**
         * 函数功能：获取当前词法单元类型名称。
         * 输入：
         * - 无。
         * 输出：词法单元类型字符串。
         */
        public String getTokenType() {
            return tokenType;
        }

        /**
         * 函数功能：获取当前词法单元文本。
         * 输入：
         * - 无。
         * 输出：词法单元文本字符串。
         */
        public String getLexeme() {
            return lexeme;
        }

        /**
         * 函数功能：获取当前词法单元偏移量。
         * 输入：
         * - 无。
         * 输出：偏移量整数。
         */
        public int getOffset() {
            return offset;
        }

        /**
         * 函数功能：获取分析动作描述。
         * 输入：
         * - 无。
         * 输出：分析动作字符串。
         */
        public String getAction() {
            return action;
        }

        /**
         * 函数功能：获取动作执行前的状态栈文本。
         * 输入：
         * - 无。
         * 输出：状态栈字符串。
         */
        public String getStackBefore() {
            return stackBefore;
        }
    }
}
