package org.harvey.vie.theory.demo.semantic.callable;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.syntax.bu.ShiftReducePhaseContext;
import org.harvey.vie.theory.syntax.callback.ShiftReduceErrorType;
import org.harvey.vie.theory.syntax.callback.ShiftReduceSemanticCallback;
import org.harvey.vie.theory.syntax.callback.tree.node.SyntaxTreeNode;
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
public class PrintSemanticCallback implements ShiftReduceSemanticCallback {
    private final Stack<SourceToken> treeContext;

    public PrintSemanticCallback() {
        treeContext = new Stack<>();
    }

    @Override
    public void onStart(ShiftReducePhaseContext context) {
    }

    @Override
    public void beforeAccept(ShiftReducePhaseContext context, SimpleGrammarProduction production) {
        context.invokeBeforeAccept(production);
        popTree(treeContext, production.getBody());
    }

    @Override
    public void onAccept(ShiftReducePhaseContext context, SimpleGrammarProduction production) {
        System.out.println("on accept. accepted production: " + production + ". Tree: " + treeContext);
    }

    @Override
    public void onReduce(ShiftReducePhaseContext context, SimpleGrammarProduction production) {
        context.invokeReduce(production);
        popTree(treeContext, production.getBody());
        treeContext.push(new SourcePlaceHolder(production.getHead()));
        System.out.println("on accept. accepted production: " + production + ". Tree: " + treeContext);
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
        SourceToken token0 = context.invokeShift(nextStatus); // same as token
        System.out.println("on shift. current token: " + token);
        this.treeContext.push(token);
    }

    @Override
    public void onError(ShiftReducePhaseContext context, ShiftReduceErrorType errorType) {
        System.err.println("on error: " + errorType);
    }

    public SyntaxTreeNode getRoot() {
        return null;
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
