package org.harvey.vie.theory.demo.semantic.callable;

import lombok.Getter;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.callback.PredicativeErrorType;
import org.harvey.vie.theory.syntax.callback.PredictiveCallback;
import org.harvey.vie.theory.syntax.callback.tree.node.GrammarSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.td.GrammarSyntaxTreeNodeBuilder;
import org.harvey.vie.theory.syntax.td.SyntaxParsingContext;
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
    private final TreeContext treeContext;
    @Getter
    private GrammarSyntaxTreeNode root;

    public TreeBuilderPredictiveCallback(LexicalConflictResolver lexicalConflictResolver) {
        this.lexicalConflictResolver = lexicalConflictResolver;
        this.treeContext = new TreeContext();
    }


    @Override
    public void onStart(SyntaxParsingContext ctx) {
        treeContext.start(ctx.getStart());
    }

    @Override
    public void onTerminal(SyntaxParsingContext ctx, TerminalSymbol terminal) {
        SourceToken token = ctx.invokeTerminal();
        GrammarSyntaxTreeNodeBuilder nodeBuilder = treeContext.popBuilder();
        nodeBuilder.setToken(token);

    }

    @Override
    public void onEpsilonProduction(SyntaxParsingContext ctx, HeadSymbol head) {
        ctx.invokeEpsilonProduction(head);
        GrammarSyntaxTreeNodeBuilder nodeBuilder = treeContext.popBuilder();
        // 表项产生 X -> ε
        nodeBuilder.setChildEpsilon();
    }

    @Override
    public void onProduction(SyntaxParsingContext ctx,  GrammarConcatenation concatenation) {
        GrammarSyntaxTreeNodeBuilder nodeBuilder = treeContext.popBuilder();
        Iterator<GrammarUnitSymbol> iter = concatenation.reverseIterator();
        while (iter.hasNext()) {
            GrammarUnitSymbol next = iter.next();
            GrammarSyntaxTreeNodeBuilder childBuilder = nodeBuilder.buildChild(next);
            treeContext.pushBuilder(childBuilder);
        }
        ctx.invokeProduction(concatenation);
    }

    @Override
    public void onAccept(SyntaxParsingContext ctx) {
        this.root = treeContext.buildTree();
    }

    @Override
    public void onError(SyntaxParsingContext ctx, PredicativeErrorType errorType) {
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
                resolveTrailingInput(ctx, token);
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
    }

    @Override
    public void beforeAccept(SyntaxParsingContext ctx) {
        ctx.invokeBeforeAccept();
    }

    private void resolveTrailingInput(SyntaxParsingContext ctx, SourceToken token) {
        try {
            throw new CompileException("Unexpected token at: " + token.hintString());
        } catch (CompileException e) {
            throw new RuntimeException(e);
        }
    }

    private void resolveTerminalConflict(SyntaxParsingContext ctx, SourceToken token) {
        GrammarSyntaxTreeNodeBuilder nodeBuilder = treeContext.popBuilder();
        // 冲突,是否进行修复?
        boolean success = lexicalConflictResolver.resolveTerminalConflict(token, nodeBuilder, ctx);
        if (success) {
            return;
        }
        System.err.println("expected: " + nodeBuilder.getGrammarSymbol().toTerminal().hint());
    }

    private void resolveEmptyProduction(SyntaxParsingContext ctx, SourceToken token) {
        GrammarSyntaxTreeNodeBuilder nodeBuilder = treeContext.popBuilder();
        boolean success = lexicalConflictResolver.resolveEmptyProduction(token, nodeBuilder, ctx);
        if (success) {
            return;
        }
        System.err.println("Situations that cannot be found in the phasing table.");
    }


    private static class TreeContext {
        public static final TerminalSymbol END_MARK = PredictiveParsingTable.END_MARK_SYMBOL;
        private final GrammarSyntaxTreeNodeBuilder root;
        private final Stack<GrammarSyntaxTreeNodeBuilder> nodeBuilderStack;

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

        public GrammarSyntaxTreeNode buildTree() {
            if (nodeBuilderStack.size() != 1 || nodeBuilderStack.pop().getGrammarSymbol() != END_MARK) {
                throw new IllegalStateException("In the case of impossible, the stack must be empty");
            }
            return root.build();
        }
    }
}
