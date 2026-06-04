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
    /**
     * 函数功能：获取当前节点锚点。
     * 输入：
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：SourceToken 类型返回值。
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

    /**
     * 函数功能：判断节点是否为头节点。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    default boolean isHead() {
        return false;
    }

    /**
     * 函数功能：判断节点是否为词法单元节点。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isToken() {
        return false;
    }

    /**
     * 函数功能：转换为头节点。
     * 输入：
     * - 无。
     * 输出：HeadNode 类型返回值。
     */

    default HeadNode toHead() {
        return (HeadNode) this;
    }

    /**
     * 函数功能：转换为词法单元节点。
     * 输入：
     * - 无。
     * 输出：TokenNode 类型返回值。
     */

    default TokenNode toToken() {
        return (TokenNode) this;
    }
}
