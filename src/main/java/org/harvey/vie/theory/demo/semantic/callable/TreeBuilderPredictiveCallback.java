package org.harvey.vie.theory.demo.semantic.callable;

import org.harvey.vie.theory.demo.semantic.node.GrammarSyntaxTreeNode;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.context.PredictiveSemanticContext;
import org.harvey.vie.theory.semantic.context.SemanticResult;
import org.harvey.vie.theory.semantic.callback.PredicativeErrorType;
import org.harvey.vie.theory.semantic.callback.PredictiveCallback;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.GrammarSyntaxTreeNodeBuilder;
import org.harvey.vie.theory.syntax.td.conflict.LexicalConflictResolver;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;

import java.util.Iterator;
import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 16:42
 */
public class TreeBuilderPredictiveCallback implements PredictiveCallback {
    private final LexicalConflictResolver lexicalConflictResolver;

    public TreeBuilderPredictiveCallback(LexicalConflictResolver lexicalConflictResolver) {
        this.lexicalConflictResolver = lexicalConflictResolver;
    }


    @Override
    public void onStart(PredictiveSemanticContext ctx) {
        TreeContext treeContext = new TreeContext();
        treeContext.start(ctx.getStart());
        ctx.setResult(treeContext);
        ctx.onStart();
    }

    @Override
    public void onTerminal(PredictiveSemanticContext ctx, TerminalSymbol terminal) {
        SourceToken token = ctx.currentToken();
        ctx.onTerminal(terminal);
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        nodeBuilder.setToken(token);

    }

    @Override
    public void onEpsilonProduction(PredictiveSemanticContext ctx, HeadSymbol head) {
        ctx.onEpsilonProduction(head);
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        // 表项产生 X -> ε
        nodeBuilder.setChildEpsilon();
    }

    @Override
    public void onProduction(PredictiveSemanticContext ctx, GrammarConcatenation concatenation) {
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        Iterator<GrammarUnitSymbol> iter = concatenation.reverseIterator();
        while (iter.hasNext()) {
            GrammarUnitSymbol next = iter.next();
            GrammarSyntaxTreeNodeBuilder childBuilder = nodeBuilder.buildChild(next);
            getTreeContext(ctx).pushBuilder(childBuilder);
        }
        ctx.onProduction(concatenation);
    }

    @Override
    public void onAccept(PredictiveSemanticContext ctx) {
        ctx.onAccept();
        getTreeContext(ctx).buildTree();
    }

    @Override
    public void onError(PredictiveSemanticContext ctx, PredicativeErrorType errorType) {
        SourceToken token;
        switch (errorType) {
            case UNDEFINED_PRODUCTION:
                // 未在表中找到合适的"产生式"
                token = ctx.currentToken();
                resolveEmptyProduction(ctx, token);
                break;
            case STACK_UNDERFLOW:
                // Stack 内容不足
                break;
            case TRAILING_INPUT_AFTER_ACCEPT:
                // accept 了, 但是还有输入
                token = ctx.currentToken();
                resolveTrailingInput(token);
                break;
            case INVALID_ACCEPTING_STATE:
                // accept 了, 但是stack还有未处理的状态
                break;
            case TERMINAL_CONFLICT:
                // terminal 冲突
                token = ctx.currentToken();
                resolveTerminalConflict(ctx, token);
                break;
        }
        ctx.onError(errorType);
    }

    @Override
    public void beforeAccept(PredictiveSemanticContext ctx) {
        ctx.beforeAccept();
    }

    private void resolveTrailingInput(SourceToken token) {
        try {
            throw new CompileException("Unexpected token at: " + token.hintString());
        } catch (CompileException e) {
            throw new RuntimeException(e);
        }
    }

    private void resolveTerminalConflict(PredictiveSemanticContext ctx, SourceToken token) {
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        // 冲突,是否进行修复?
        boolean success = lexicalConflictResolver.resolveTerminalConflict(token, nodeBuilder, null);
        if (success) {
            return;
        }
        System.err.println("expected: " + nodeBuilder.getGrammarSymbol().toTerminal().hint());
    }

    private void resolveEmptyProduction(PredictiveSemanticContext ctx, SourceToken token) {
        GrammarSyntaxTreeNodeBuilder nodeBuilder = getTreeContext(ctx).popBuilder();
        boolean success = lexicalConflictResolver.resolveEmptyProduction(token, nodeBuilder, null);
        if (success) {
            return;
        }
        System.err.println("Situations that cannot be found in the phasing table.");
    }

    private static TreeContext getTreeContext(PredictiveSemanticContext ctx) {
        return (TreeContext) ctx.getResult();
    }


    private static class TreeContext implements SemanticResult {
        public static final TerminalSymbol END_MARK = PredictiveParsingTable.END_MARK_SYMBOL;
        private final GrammarSyntaxTreeNodeBuilder root;
        private final Stack<GrammarSyntaxTreeNodeBuilder> nodeBuilderStack;
        private GrammarSyntaxTreeNode result;

        public TreeContext() {
            // --tree--
            this.nodeBuilderStack = new Stack<>();
            this.root = new GrammarSyntaxTreeNodeBuilder(END_MARK);
            nodeBuilderStack.push(root);
        }

        public void start(GrammarUnitSymbol start) {
            nodeBuilderStack.push(root.buildChild(start));
        }

        public GrammarSyntaxTreeNodeBuilder popBuilder() {
            return nodeBuilderStack.pop();
        }

        public void pushBuilder(GrammarSyntaxTreeNodeBuilder builder) {
            nodeBuilderStack.push(builder);
        }

        public void buildTree() {
            if (nodeBuilderStack.size() != 1 || nodeBuilderStack.pop().getGrammarSymbol() != END_MARK) {
                throw new IllegalStateException("In the case of impossible, the stack must be empty");
            }
            this.result = root.build();
        }

        @Override
        public String toString() {
            return result.toString();
        }
    }
}
