package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.semantic.sequence.SequnceStep;
import org.harvey.vie.theory.semantic.sequence.Stepper;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-25 14:00
 */
public class ArgumentStepper implements Stepper<HeadNode> {
    @Override
    public SequnceStep<HeadNode> step(HeadNode head) {
        if (head.matchTags(ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.VALUE)) {
            return SequnceStep.item(head, null);
        }
        if (head.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.EMPTY)) {
            return SequnceStep.stop();
        }
        if (head.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.FORWARD)) {
            return SequnceStep.advance(head.get(0));
        }
        if (head.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.SEQUENCE)) {
            return SequnceStep.item(head.get(1).toHead(), head.get(0));
        }
        return SequnceStep.stop();
    }
}
