package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Type attributes bound to concrete syntax tree nodes.
 *
 * @author Temper
 */
public class TypeContext {
    private final Map<ShiftReduceSyntaxTreeNode, TypeRegister> attributes = new IdentityHashMap<>();

    public void bind(ShiftReduceSyntaxTreeNode node, TypeRegister register) {
        if (node == null) {
            throw new IllegalArgumentException("node must not be null");
        }
        if (register == null) {
            attributes.remove(node);
            return;
        }
        attributes.put(node, register);
    }

    public TypeRegister get(ShiftReduceSyntaxTreeNode node) {
        return attributes.get(node);
    }

    public boolean has(ShiftReduceSyntaxTreeNode node) {
        return attributes.containsKey(node);
    }

    public void move(ShiftReduceSyntaxTreeNode from, ShiftReduceSyntaxTreeNode to) {
        if (from == to) {
            return;
        }
        TypeRegister register = attributes.remove(from);
        if (register != null) {
            attributes.put(to, register);
        }
    }
}

