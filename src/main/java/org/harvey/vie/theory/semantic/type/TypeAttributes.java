package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Helper for reading semantic type attributes from the current reduced node.
 */
public final class TypeAttributes {
    private TypeAttributes() {
    }

    public static TypeRegister child(ShiftReduceSemanticContext context, int index) {
        ShiftReduceSyntaxTreeNode child = reducedHead(context).get(index);
        TypeRegister register = context.getType(child);
        if (register == null) {
            throw new IllegalStateException("semantic type is absent for child #" + index);
        }
        return register;
    }

    public static SemanticType childType(ShiftReduceSemanticContext context, int index) {
        return child(context, index).requireType("semantic type is required for child #" + index + " but the child has no type.");
    }

    public static SemanticType childInstructionType(ShiftReduceSemanticContext context, int index) {
        return child(context, index)
                .requireInstructionType("instruction type is required for child #" + index + " but the child has no instruction type.");
    }

    public static SourceToken childAnchor(ShiftReduceSemanticContext context, int index) {
        return anchorOf(reducedHead(context).get(index));
    }

    public static TypeRegister result(ShiftReduceSemanticContext context) {
        HeadNode head = reducedHead(context);
        TypeRegister register = context.getType(head);
        if (register == null) {
            throw new IllegalStateException("semantic type is absent for current reduced head");
        }
        return register;
    }

    public static boolean childHasType(ShiftReduceSemanticContext context, int index) {
        return context.hasType(reducedHead(context).get(index));
    }

    public static SourceToken resultAnchor(ShiftReduceSemanticContext context) {
        return anchorOf(reducedHead(context));
    }

    private static HeadNode reducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }

    private static SourceToken anchorOf(ShiftReduceSyntaxTreeNode node) {
        Queue<ShiftReduceSyntaxTreeNode> queue = new ArrayDeque<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            ShiftReduceSyntaxTreeNode current = queue.remove();
            if (current.isToken()) {
                return current.toToken().getSource();
            }
            if (!current.isHead()) {
                continue;
            }
            for (ShiftReduceSyntaxTreeNode child : current.toHead()) {
                queue.add(child);
            }
        }
        throw new IllegalStateException("syntax tree node has no token anchor.");
    }
}
