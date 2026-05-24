package org.harvey.vie.theory.semantic.tree.node;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 01:01
 */
public interface ShiftReduceSyntaxTreeNode {
    default boolean isHead() {
        return false;
    }

    default boolean isToken() {
        return false;
    }

    default HeadNode toHead() {
        return (HeadNode) this;
    }

    default TokenNode toToken() {
        return (TokenNode) this;
    }


    /**
     * 用于获取一个Token, 一般可以用在抛出异常的地方使用
     */
    static SourceToken anchor(ShiftReduceSyntaxTreeNode node) {
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
