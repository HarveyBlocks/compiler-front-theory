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

    public List<TraceEntry> snapshot() {
        return List.copyOf(entries);
    }

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

        public TraceEntry(String phase, String tokenType, String lexeme, int offset, String action, String stackBefore) {
            this.phase = phase;
            this.tokenType = tokenType;
            this.lexeme = lexeme;
            this.offset = offset;
            this.action = action;
            this.stackBefore = stackBefore;
        }

        public String getPhase() {
            return phase;
        }

        public String getTokenType() {
            return tokenType;
        }

        public String getLexeme() {
            return lexeme;
        }

        public int getOffset() {
            return offset;
        }

        public String getAction() {
            return action;
        }

        public String getStackBefore() {
            return stackBefore;
        }
    }
}
