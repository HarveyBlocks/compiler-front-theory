package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * @author Temper
 */
public final class FunctionReturnFlowAnalyzer {
    private final ProductionTagStrategy<ReturnRule> rules;

    public FunctionReturnFlowAnalyzer() {
        ReturnRule never = (context, head) -> false;
        ReturnRule returnRule = (context, head) -> true;
        ReturnRule block = this::blockGuaranteesReturn;
        ReturnRule blockItemsSequence = this::blockItemsSequenceGuaranteesReturn;
        ReturnRule forward = this::forwardGuaranteesReturn;
        ReturnRule matchIf = this::matchedIfGuaranteesReturn;
        rules = new ProductionTagStrategy<>(never)
                .when(block, ProgramSemanticTag.BLOCK, ProgramSemanticTag.COMMAND)
                .when(never, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
                .when(
                        blockItemsSequence,
                        ProgramSemanticTag.BLOCK,
                        ProgramSemanticTag.LIST,
                        ProgramSemanticTag.SEQUENCE
                )
                .when(forward, ProgramSemanticTag.FORWARD)
                .when(returnRule, ProgramSemanticTag.RETURN)
                .when(matchIf, ProgramSemanticTag.CONDITIONAL, ProgramSemanticTag.ELSE_BRANCH);
    }

    @FunctionalInterface
    private interface ReturnRule {
        boolean test(ShiftReduceSemanticContext context, HeadNode head);
    }


    public boolean guaranteesReturn(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        if (node == null || !node.isHead()) {
            return false;
        }
        HeadNode head = node.toHead();
        return rules.resolve(head.getProduction()).test(context, head);
    }

    public boolean blockGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        return guaranteesReturn(context, head.get(1));
    }

    public boolean blockItemsSequenceGuaranteesReturn(
            ShiftReduceSemanticContext context, HeadNode head) {
        return guaranteesReturn(context, head.get(0)) || guaranteesReturn(context, head.get(1));
    }

    public boolean forwardGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        for (ShiftReduceSyntaxTreeNode child : head) {
            if (guaranteesReturn(context, child)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchedIfGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        Boolean condition = constantBoolean(context, head.get(2));
        if (Boolean.TRUE.equals(condition)) {
            return guaranteesReturn(context, head.get(4));
        }
        if (Boolean.FALSE.equals(condition)) {
            return guaranteesReturn(context, head.get(6));
        }
        return guaranteesReturn(context, head.get(4)) && guaranteesReturn(context, head.get(6));
    }

    public Boolean constantBoolean(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        ConstantValue value = context.getConstantValue(node);
        if (value == null || !value.getType().isBooleanScalar()) {
            return null;
        }
        return value.bool();
    }

}

