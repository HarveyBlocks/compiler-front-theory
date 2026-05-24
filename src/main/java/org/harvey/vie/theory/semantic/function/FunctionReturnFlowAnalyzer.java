package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.value.ConstantValue;

public final class FunctionReturnFlowAnalyzer {
    private static final ProductionTagStrategy<ReturnRule> RULES = new ProductionTagStrategy<>(ReturnRule.NEVER)
            .when(ReturnRule.BLOCK, ProgramSemanticTag.BLOCK, ProgramSemanticTag.COMMAND)
            .when(ReturnRule.BLOCK_ITEMS_EMPTY,
                    ProgramSemanticTag.BLOCK,
                    ProgramSemanticTag.LIST,
                    ProgramSemanticTag.EMPTY
            )
            .when(ReturnRule.BLOCK_ITEMS_SEQUENCE,
                    ProgramSemanticTag.BLOCK,
                    ProgramSemanticTag.LIST,
                    ProgramSemanticTag.SEQUENCE
            )
            .when(ReturnRule.FORWARD, ProgramSemanticTag.FORWARD)
            .when(ReturnRule.RETURN, ProgramSemanticTag.RETURN)
            .when(ReturnRule.MATCHED_IF, ProgramSemanticTag.CONDITIONAL, ProgramSemanticTag.ELSE_BRANCH);

    private FunctionReturnFlowAnalyzer() {
    }

    public static boolean guaranteesReturn(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        if (node == null || !node.isHead()) {
            return false;
        }
        HeadNode head = node.toHead();
        return RULES.resolve(head.getProduction()).test(context, head);
    }

    private static boolean blockGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        return guaranteesReturn(context, head.get(1));
    }

    private static boolean blockItemsSequenceGuaranteesReturn(
            ShiftReduceSemanticContext context, HeadNode head) {
        return guaranteesReturn(context, head.get(0)) || guaranteesReturn(context, head.get(1));
    }

    private static boolean forwardGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        for (ShiftReduceSyntaxTreeNode child : head) {
            if (guaranteesReturn(context, child)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchedIfGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        Boolean condition = constantBoolean(context, head.get(2));
        if (Boolean.TRUE.equals(condition)) {
            return guaranteesReturn(context, head.get(4));
        }
        if (Boolean.FALSE.equals(condition)) {
            return guaranteesReturn(context, head.get(6));
        }
        return guaranteesReturn(context, head.get(4)) && guaranteesReturn(context, head.get(6));
    }

    private static Boolean constantBoolean(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        ConstantValue value = context.getConstantValue(node);
        if (value == null || !value.getType().isBooleanScalar()) {
            return null;
        }
        return value.bool();
    }

    @FunctionalInterface
    private interface ReturnRule {
        ReturnRule NEVER = (context, head) -> false;
        ReturnRule RETURN = (context, head) -> true;
        ReturnRule BLOCK = FunctionReturnFlowAnalyzer::blockGuaranteesReturn;
        ReturnRule BLOCK_ITEMS_EMPTY = NEVER;
        ReturnRule BLOCK_ITEMS_SEQUENCE = FunctionReturnFlowAnalyzer::blockItemsSequenceGuaranteesReturn;
        ReturnRule FORWARD = FunctionReturnFlowAnalyzer::forwardGuaranteesReturn;
        ReturnRule MATCHED_IF = FunctionReturnFlowAnalyzer::matchedIfGuaranteesReturn;

        boolean test(ShiftReduceSemanticContext context, HeadNode head);
    }
}
