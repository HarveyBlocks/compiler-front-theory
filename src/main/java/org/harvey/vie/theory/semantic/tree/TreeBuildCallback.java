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
            /**
             * 函数功能：获取语义栈上下文。
             * 输入：
             * - context：ShiftReduceSemanticContext 类型参数。
             * 输出：Stack<ShiftReduceSyntaxTreeNode> 类型返回值。
             */
            @Override
            public Stack<ShiftReduceSyntaxTreeNode> getStackContext(ShiftReduceSemanticContext context) {
                return context.getTreeContext();
            }
/**
 * 函数功能：创建子节点数组。
 * 输入：
 * - n：int 类型参数。
 * 输出：ShiftReduceSyntaxTreeNode[] 类型数组。
 */

            @Override
            public ShiftReduceSyntaxTreeNode[] instanceChildrenArray(int n) {
                return new ShiftReduceSyntaxTreeNode[n];
            }
/**
 * 函数功能：在规约时创建语法树节点。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - production：SimpleGrammarProduction 类型参数。
 * - children：ShiftReduceSyntaxTreeNode[] 类型参数。
 * 输出：ShiftReduceSyntaxTreeNode 类型返回值。
 */

            @Override
            public ShiftReduceSyntaxTreeNode instanceNodeOnReduce(
                    ShiftReduceSemanticContext context,
                    SimpleGrammarProduction production,
                    ShiftReduceSyntaxTreeNode[] children) {
                return new HeadNode(production.getHead(), production, children);
            }
/**
 * 函数功能：在移进时创建语法树节点。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - token：SourceToken 类型参数。
 * 输出：ShiftReduceSyntaxTreeNode 类型返回值。
 */

            @Override
            public ShiftReduceSyntaxTreeNode instanceNodeOnShift(
                    ShiftReduceSemanticContext context, SourceToken token) {
                return new TokenNode(token);
            }
        };
    }
/**
 * 函数功能：创建 TreeBuildCallback 对象。
 * 输入：
 * - 无。
 * 输出：无。
 */



    public TreeBuildCallback() {
        super(SUPPLIER);
    }
}
