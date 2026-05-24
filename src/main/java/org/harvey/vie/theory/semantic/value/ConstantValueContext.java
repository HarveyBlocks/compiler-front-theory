package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Stores compile-time constant values on concrete syntax nodes.
 *
 * @author Temper
 */
public class ConstantValueContext {
    private final Map<ShiftReduceSyntaxTreeNode, ConstantValue> values = new IdentityHashMap<>();

    public void bind(ShiftReduceSyntaxTreeNode node, ConstantValue value) {
        if (node == null) {
            throw new IllegalArgumentException("node must not be null");
        }
        if (value == null) {
            values.remove(node);
            return;
        }
        values.put(node, value);
    }

    public ConstantValue get(ShiftReduceSyntaxTreeNode node) {
        return values.get(node);
    }

    public boolean has(ShiftReduceSyntaxTreeNode node) {
        return values.containsKey(node);
    }
}

