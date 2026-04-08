package org.harvey.vie.theory.demo.semantic.callable;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.semantic.SemanticResult;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaseContext;
import org.harvey.vie.theory.syntax.callback.ShiftReduceCallback;
import org.harvey.vie.theory.syntax.callback.ShiftReduceErrorType;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 12:24
 */
public class PrintCallback implements ShiftReduceCallback {


    public PrintCallback() {

    }

    @Override
    public void onStart(ShiftReducePhaseContext context) {
        context.onStart();
        context.setResult(new TreeContext());
    }

    @Override
    public void beforeAccept(ShiftReducePhaseContext context, SimpleGrammarProduction production) {
        context.beforeAccept(production);
        popTree(treeContext(context), production.getBody());
    }

    @Override
    public void onAccept(ShiftReducePhaseContext context, SimpleGrammarProduction production) {
        context.onAccept(production);
        System.out.println("on accept. accepted production: " + production + ". Tree: " + treeContext(context));
    }

    @Override
    public void onReduce(ShiftReducePhaseContext context, SimpleGrammarProduction production) {
        context.onReduce(production);
        popTree(treeContext(context), production.getBody());
        treeContext(context).push(new SourcePlaceHolder(production.getHead()));
        System.out.println("on reduce. accepted production: " + production + ". Tree: " + treeContext(context));
    }

    private void popTree(Stack<SourceToken> treeContext, AlterableSymbol body) {
        int k = body.isEpsilon() ? 0 : body.toConcatenation().size();
        while (k-- > 0) {
            if (treeContext.isEmpty()) {
                throw new CompilerException("no more status in stack to be pop while reducing");
            }
            treeContext.pop();
        }
    }

    @Override
    public void onShift(ShiftReducePhaseContext context, int nextStatus, SourceToken token) {
        context.onShift(nextStatus, token);
        System.out.println("on shift. current token: " + token);
        this.treeContext(context).push(token);
    }

    private Stack<SourceToken> treeContext(ShiftReducePhaseContext context) {
        return ((TreeContext) context.getResult()).stack;
    }

    @Override
    public void onError(ShiftReducePhaseContext context, ShiftReduceErrorType errorType) {
        System.err.println("on error: " + errorType);
        context.onError(errorType);
    }

    private static class TreeContext implements SemanticResult {
        private final Stack<SourceToken> stack = new Stack<>();

        @Override
        public String toString() {
            return stack.toString();
        }
    }

    @AllArgsConstructor
    private static class SourcePlaceHolder implements SourceToken {
        private final HeadSymbol headSymbol;

        @Override
        public String hintString() {
            return "SPECIAL[`" + headSymbol + "`]";
        }

        @Override
        public String toString() {
            return hintString();
        }

        @Override
        public byte[] getLexeme() {
            return new byte[0];
        }

        @Override
        public int getOffset() {
            return -1;
        }

        @Override
        public TokenType getType() {
            return null;
        }
    }

}
