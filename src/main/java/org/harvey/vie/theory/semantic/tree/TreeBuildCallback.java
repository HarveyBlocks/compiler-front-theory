package org.harvey.vie.theory.semantic.tree;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.BuildStackContextCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.tree.node.TokenNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.Stack;


/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 12:24
 */
public class TreeBuildCallback extends BuildStackContextCallback<ShiftReduceSyntaxTreeNode> implements
        ShiftReduceCallback {
    private static final Supplier<ShiftReduceSyntaxTreeNode> SUPPLIER;

    static {
        SUPPLIER = new Supplier<>() {
            @Override
            public Stack<ShiftReduceSyntaxTreeNode> getStackContext(ShiftReduceSemanticContext context) {
                return context.getTreeContext();
            }

            @Override
            public ShiftReduceSyntaxTreeNode[] instanceChildrenArray(int n) {
                return new ShiftReduceSyntaxTreeNode[n];
            }

            @Override
            public ShiftReduceSyntaxTreeNode instanceNodeOnReduce(
                    ShiftReduceSemanticContext context,
                    SimpleGrammarProduction production,
                    ShiftReduceSyntaxTreeNode[] children) {
                return new HeadNode(production.getHead(), children);
            }

            @Override
            public ShiftReduceSyntaxTreeNode instanceNodeOnShift(
                    ShiftReduceSemanticContext context, SourceToken token) {
                return new TokenNode(token);
            }
        };
    }

    private static final Visitor<ShiftReduceSyntaxTreeNode> VISITOR;

    static {
        VISITOR = new Visitor<>() {
        };
    }

    public TreeBuildCallback() {
        super(SUPPLIER, VISITOR);
    }
}
