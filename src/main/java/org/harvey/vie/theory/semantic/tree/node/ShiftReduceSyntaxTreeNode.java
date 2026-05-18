package org.harvey.vie.theory.semantic.tree.node;

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
}
