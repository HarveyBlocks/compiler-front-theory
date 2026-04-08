package org.harvey.vie.theory.demo.semantic.node;

/**
 * TODO 有孩子
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 18:41
 */
public interface HeadNode extends GrammarSyntaxTreeNode {
    GrammarSyntaxTreeNode[] children();

    GrammarSyntaxTreeNode child(int i);
}
