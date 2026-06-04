package org.harvey.vie.theory.demo.semantic.node;

import java.util.Arrays;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-02 04:23
 */
public class HeadNodeImpl implements HeadNode {
    private final GrammarSyntaxTreeNode[] children;

    /**
     * 函数功能：创建包含指定子节点的非终结节点。
     * 输入：
     * - children：当前节点的子节点数组。
     * 输出：无。
     */
    public HeadNodeImpl(GrammarSyntaxTreeNode[] children) {this.children = children;}

    /**
     * 函数功能：获取当前非终结节点的全部子节点。
     * 输入：
     * - 无。
     * 输出：GrammarSyntaxTreeNode 子节点数组。
     */
    @Override
    public GrammarSyntaxTreeNode[] children() {
        return children;
    }

    /**
     * 函数功能：获取指定位置的子节点。
     * 输入：
     * - i：子节点索引。
     * 输出：指定索引处的 GrammarSyntaxTreeNode。
     */
    @Override
    public GrammarSyntaxTreeNode child(int i) {
        return children[i];
    }

    /**
     * 函数功能：获取非终结节点的字符串表示。
     * 输入：
     * - 无。
     * 输出：子节点数组的字符串表示。
     */
    @Override
    public String toString() {
        return Arrays.toString(children);
    }
}
