package org.harvey.vie.theory.semantic.structure;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.semantic.sequence.SequnceStep;
import org.harvey.vie.theory.semantic.sequence.Stepper;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

/**
 * Iterates list-shaped struct field syntax nodes.
 *
 * @author Temper
 */
public class StructFieldStepper implements Stepper<HeadNode> {
    /**
     * 函数功能：推进并返回当前序列步骤。
     * 输入：
     * - head：HeadNode 类型参数。
     * 输出：SequnceStep<HeadNode> 类型返回值。
     */
    @Override
    public SequnceStep<HeadNode> step(HeadNode head) {
        if (head.matchTags(ProgramSemanticTag.STRUCT_FIELD, ProgramSemanticTag.IDENTIFIER)) {
            return SequnceStep.item(head, null);
        }
        if (head.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.STRUCT_FIELD, ProgramSemanticTag.EMPTY)) {
            return SequnceStep.stop();
        }
        if (head.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.STRUCT_FIELD, ProgramSemanticTag.FORWARD)) {
            return SequnceStep.advance(head.get(0));
        }
        if (head.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.STRUCT_FIELD, ProgramSemanticTag.SEQUENCE)) {
            return SequnceStep.defer(head.get(1).toHead(), head.get(0));
        }
        return SequnceStep.stop();
    }
}
