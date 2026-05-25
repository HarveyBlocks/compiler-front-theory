package org.harvey.vie.theory.semantic.sequence;

import org.harvey.vie.theory.semantic.tree.node.HeadNode;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 14:00
 */
@FunctionalInterface
public interface Stepper<T> {
    SequnceStep<T> step(HeadNode head);
}
