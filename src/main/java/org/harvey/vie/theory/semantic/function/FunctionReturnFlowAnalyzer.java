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


    /**
     * 从当前节点开始判断该子树是否一定返回。
     *
     * @param context 语义上下文，用于查询常量折叠结果
     * @param node 待分析的语法树节点
     * @return 当所有可达执行路径都会命中 return 时返回 true
     */
    public boolean guaranteesReturn(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        if (node == null || !node.isHead()) {
            return false;
        }
        HeadNode head = node.toHead();
        return rules.resolve(head.getProduction()).test(context, head);
    }

    /**
     * 代码块是否保证返回，取决于其内部语句序列是否保证返回。
     */
    public boolean blockGuaranteesReturn(ShiftReduceSemanticContext context, HeadNode head) {
        return guaranteesReturn(context, head.get(1));
    }

    /**
     * 顺序语句中只要前半段已经保证返回，后半段就不可达；
     * 否则继续检查后续语句是否补足返回路径。
     */
    public boolean blockItemsSequenceGuaranteesReturn(
            ShiftReduceSemanticContext context, HeadNode head) {
        return guaranteesReturn(context, head.get(0)) || guaranteesReturn(context, head.get(1));
    }

    /**
     * 顺着 forward 产生式向下寻找第一个能够保证返回的子节点。
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
     * 对带 else 的条件分支做返回流分析。
     * <p>
     * 如果条件已经被常量传播折叠为 true/false，只检查可达分支；
     * 否则要求 then 和 else 两个分支都保证返回。
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
     * 读取表达式的布尔常量值；无法在编译期确定时返回 null。
     */
    public Boolean constantBoolean(ShiftReduceSemanticContext context, ShiftReduceSyntaxTreeNode node) {
        ConstantValue value = context.getConstantValue(node);
        if (value == null || !value.getType().isBooleanScalar()) {
            return null;
        }
        return value.bool();
    }

}

