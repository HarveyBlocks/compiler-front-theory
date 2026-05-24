package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

/**
 * Helper for reading constant-value attributes from the current reduced node.
 *
 * @author Temper
 */
public final class ConstantAttributes {
    private ConstantAttributes() {
    }

    public static ConstantValue child(ShiftReduceSemanticContext context, int index) {
        ShiftReduceSyntaxTreeNode child = reducedHead(context).get(index);
        return context.getConstantValue(child);
    }

    public static boolean childIsConstant(ShiftReduceSemanticContext context, int index) {
        ShiftReduceSyntaxTreeNode child = reducedHead(context).get(index);
        return context.hasConstantValue(child);
    }

    public static ConstantValue result(ShiftReduceSemanticContext context) {
        return context.getConstantValue(reducedHead(context));
    }

    private static HeadNode reducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }
}

