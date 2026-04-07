package org.harvey.vie.theory.syntax.callback.tree.node;

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

    public HeadNodeImpl(GrammarSyntaxTreeNode[] children) {this.children = children;}

    @Override
    public GrammarSyntaxTreeNode[] children() {
        return children;
    }

    @Override
    public GrammarSyntaxTreeNode child(int i) {
        return children[i];
    }

    @Override
    public String toString() {
        return Arrays.toString(children);
    }
}
