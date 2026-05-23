package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.value.ConstantValue;

public final class FunctionReturnFlowAnalyzer {
    private FunctionReturnFlowAnalyzer() {
    }

    public static boolean guaranteesReturn(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        if (node == null || !node.isHead()) {
            return false;
        }
        HeadNode head = node.toHead();
        String symbol = head.getSymbol().toString();
        switch (symbol) {
            case "block":
                return blockGuaranteesReturn(context, head);
            case "block_items":
                return blockItemsGuaranteeReturn(context, head);
            case "block_item":
                return head.size() > 0 && guaranteesReturn(context, head.get(0));
            case "stmt":
            case "matched_stmt":
            case "unmatched_stmt":
                return statementGuaranteesReturn(context, head);
            case "return_stmt":
                return true;
            case "matched_if_stmt":
                return matchedIfGuaranteesReturn(context, head);
            default:
                return false;
        }
    }

    private static boolean blockGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        if (head.size() < 2) {
            return false;
        }
        return guaranteesReturn(context, head.get(1));
    }

    private static boolean blockItemsGuaranteeReturn(ShiftReduceSemanticContext context, HeadNode head) {
        if (head.size() == 0) {
            return false;
        }
        if (head.size() == 1) {
            return guaranteesReturn(context, head.get(0));
        }
        return guaranteesReturn(context, head.get(0)) || guaranteesReturn(context, head.get(1));
    }

    private static boolean statementGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        String symbol = head.getSymbol().toString();
        if ("matched_stmt".equals(symbol) && head.size() == 1 && head.get(0).isHead()) {
            HeadNode child = head.get(0).toHead();
            if ("return_stmt".equals(child.getSymbol().toString())) {
                return true;
            }
            if ("matched_if_stmt".equals(child.getSymbol().toString())) {
                return matchedIfGuaranteesReturn(context, child);
            }
            if ("block".equals(child.getSymbol().toString())) {
                return blockGuaranteesReturn(context, child);
            }
        }
        if (head.size() == 1) {
            return guaranteesReturn(context, head.get(0));
        }
        return false;
    }

    private static boolean matchedIfGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        if (head.size() != 7) {
            return false;
        }
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
}
