package org.harvey.vie.theory.semantic.tree.node;

import java.util.Stack;
import java.util.function.Function;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 01:01
 */
public class TreeContext extends Stack<ShiftReduceSyntaxTreeNode> {

    public void resetTop(Function<ShiftReduceSyntaxTreeNode,ShiftReduceSyntaxTreeNode> mapper) {
        push(mapper.apply(pop()));
    }
}
