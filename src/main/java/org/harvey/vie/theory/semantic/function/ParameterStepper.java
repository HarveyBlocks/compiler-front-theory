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
public class ParameterStepper implements Stepper<HeadNode> {
    /**
     * 函数功能：推进并返回当前序列步骤。
     * 输入：
     * - head：HeadNode 类型参数。
     * 输出：SequnceStep<HeadNode> 类型返回值。
     */
    @Override
    public SequnceStep<HeadNode> step(HeadNode head) {
        if (head.matchTags(ProgramSemanticTag.PARAMETER, ProgramSemanticTag.IDENTIFIER)) {
            return SequnceStep.item(head, null);
        }
        if (head.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.PARAMETER, ProgramSemanticTag.EMPTY)) {
            return SequnceStep.stop();
        }
        if (head.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.PARAMETER, ProgramSemanticTag.FORWARD)) {
            return SequnceStep.advance(head.get(0));
        }
        if (head.matchTags(ProgramSemanticTag.LIST, ProgramSemanticTag.PARAMETER, ProgramSemanticTag.SEQUENCE)) {
            return SequnceStep.defer(head.get(2).toHead(), head.get(0));
        }
        return SequnceStep.stop();
    }
}
