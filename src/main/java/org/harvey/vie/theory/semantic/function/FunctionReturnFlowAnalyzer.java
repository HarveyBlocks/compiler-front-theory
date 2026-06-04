package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * 判断一棵语法子树是否能够在所有可达路径上保证执行到 {@code return}。
 * <p>
 * 该分析器不直接遍历具体语法类型，而是把产生式标签映射为不同的判定规则，
 * 这样在扩展语法时只需要补充标签到规则的关联关系。
 *
 * @author Temper
 */
public final class FunctionReturnFlowAnalyzer {
    private final ProductionTagStrategy<ReturnRule> rules;
/**
 * 函数功能：创建 FunctionReturnFlowAnalyzer 对象。
 * 输入：
 * - 无。
 * 输出：无。
 */

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
        /**
         * 函数功能：判断输入是否满足条件。
         * 输入：
         * - context：ShiftReduceSemanticContext 类型参数。
         * - head：HeadNode 类型参数。
         * 输出：判断结果布尔值。
         */
        boolean test(ShiftReduceSemanticContext context, HeadNode head);
    }


    /**
     * 函数功能：判断语法节点是否保证返回。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean guaranteesReturn(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        if (node == null || !node.isHead()) {
            return false;
        }
        HeadNode head = node.toHead();
        return rules.resolve(head.getProduction()).test(context, head);
    }

    /**
     * 函数功能：判断代码块是否保证返回。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean blockGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        return guaranteesReturn(context, head.get(1));
    }

    /**
     * 函数功能：判断代码块语句序列是否保证返回。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean blockItemsSequenceGuaranteesReturn(
            ShiftReduceSemanticContext context, HeadNode head) {
        return guaranteesReturn(context, head.get(0)) || guaranteesReturn(context, head.get(1));
    }

    /**
     * 函数功能：判断顺序语句是否保证返回。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：判断结果布尔值。
     */
    public boolean forwardGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        for (ShiftReduceSyntaxTreeNode child : head) {
            if (guaranteesReturn(context, child)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 函数功能：判断匹配的 if 语句是否保证返回。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：判断结果布尔值。
     */
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

    /**
     * 函数功能：获取常量布尔值。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - node：ShiftReduceSyntaxTreeNode 类型参数。
     * 输出：判断结果布尔值。
     */
    public Boolean constantBoolean(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        ConstantValue value = context.getConstantValue(node);
        if (value == null || !value.getType().isBooleanScalar()) {
            return null;
        }
        return value.bool();
    }

}

