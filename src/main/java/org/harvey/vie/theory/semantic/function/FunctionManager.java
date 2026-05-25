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

    public FunctionManager(FunctionReturnFlowAnalyzer returnFlowAnalyzer) {this.returnFlowAnalyzer = returnFlowAnalyzer;}

    public boolean guaranteesReturn(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        return returnFlowAnalyzer.guaranteesReturn(context, node);
    }
}
