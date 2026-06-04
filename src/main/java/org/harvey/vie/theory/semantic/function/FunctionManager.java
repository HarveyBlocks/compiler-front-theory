package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 11:09
 */
public class FunctionManager {
    private final FunctionReturnFlowAnalyzer returnFlowAnalyzer;

    /**
     * 函数功能：创建 FunctionManager 对象。
     * 输入：
     * - returnFlowAnalyzer：FunctionReturnFlowAnalyzer 类型参数。
     * 输出：无。
     */

    public FunctionManager(FunctionReturnFlowAnalyzer returnFlowAnalyzer) {this.returnFlowAnalyzer = returnFlowAnalyzer;}

    /**
     * 函数功能：判断语法节点是否保证返回。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：判断结果布尔值。
     */

    public boolean guaranteesReturn(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        return returnFlowAnalyzer.guaranteesReturn(context, node);
    }
}
