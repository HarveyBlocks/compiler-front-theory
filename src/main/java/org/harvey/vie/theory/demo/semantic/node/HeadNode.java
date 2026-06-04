package org.harvey.vie.theory.demo.semantic.node;

/**
 * TODO 有孩子
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 18:41
 */
public interface HeadNode extends GrammarSyntaxTreeNode {
    /**
     * 函数功能：获取当前非终结节点的全部子节点。
     * 输入：
     * - 无。
     * 输出：GrammarSyntaxTreeNode 子节点数组。
     */
    GrammarSyntaxTreeNode[] children();

    /**
     * 函数功能：获取指定位置的子节点。
     * 输入：
     * - i：子节点索引。
     * 输出：指定索引处的 GrammarSyntaxTreeNode。
     */
    GrammarSyntaxTreeNode child(int i);
}
