package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.demo.program.ProgramSemanticTag;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenStringMapping;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.ProductionTagStrategy;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 在 LR 归约过程中构造编译期常量属性的语义回调。
 * <p>
 * 作用：
 * <p>
 * ConstantValueBuildCallback 负责常量折叠和一部分常量传播。
 * 每当语法分析器完成一次 reduce，它会根据产生式语义 tag 判断当前节点是否可以
 * 在编译期确定值。如果可以，就把 ConstantValue 绑定到当前 HeadNode 上。
 * <p>
 * 这与实验任务书中“语法制导翻译及中间代码生成”的目标相配合：
 * <p>
 * 1. 语义分析阶段识别常量表达式。
 * 2. 中间代码生成阶段可以直接装载常量。
 * 3. 避免为已经确定的表达式生成多余运行期求值指令。
 * <p>
 * 注意：
 * <p>
 * 该类不负责类型合法性检查。
 * 类型检查主要由 TypeBuildCallback 和其他语义 callback 完成。
 * 如果某个表达式不能安全折叠，规则会返回 null，让后续阶段走普通翻译流程。
 */
public class ConstantValueBuildCallback implements ShiftReduceCallback {
    /**
     * production tag 到常量构造规则的映射表。
     * <p>
     * 输入：
     * <p>
     * 当前归约产生式携带的 ProgramSemanticTag 组合。
     * <p>
     * 输出：
     * <p>
     * 解析出一个 ConstantRule，用于尝试构造当前节点的 ConstantValue。
     * <p>
     * 注意：
     * <p>
     * 大部分语句、声明、函数调用、新建对象等节点不产生编译期常量，
     * 因而映射到 ConstantRule.NONE。
     */
    private static final ProductionTagStrategy<ConstantRule> RULES = new ProductionTagStrategy<>(ConstantRule.NONE)
            .when(ConstantRule.NONE, ProgramSemanticTag.PROGRAM)
            .when(ConstantRule.NONE, ProgramSemanticTag.NOOP)
            .when(ConstantRule.NONE, ProgramSemanticTag.DECLARATION)
            .when(ConstantRule.NONE, ProgramSemanticTag.RETURN)
            .when(ConstantRule.NONE, ProgramSemanticTag.LOOP)
            .when(ConstantRule.NONE, ProgramSemanticTag.CONDITIONAL)
            .when(ConstantRule.NONE, ProgramSemanticTag.BLOCK, ProgramSemanticTag.COMMAND)
            .when(ConstantRule.NONE, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
            .when(ConstantRule.NONE, ProgramSemanticTag.BLOCK, ProgramSemanticTag.LIST, ProgramSemanticTag.SEQUENCE)
            .when(ConstantRule.NONE, ProgramSemanticTag.PARAMETER)
            .when(ConstantRule.NONE, ProgramSemanticTag.STRUCT_DECL)
            .when(ConstantRule.NONE, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.HEAD)
            .when(ConstantRule.NONE, ProgramSemanticTag.FUNCTION, ProgramSemanticTag.CALL)
            .when(ConstantRule.NONE, ProgramSemanticTag.NEW_STRUCT)
            .when(ConstantRule.NONE, ProgramSemanticTag.ACCESS)
            .when(ConstantRule.NONE, ProgramSemanticTag.MEMBER_ACCESS)
            .when(ConstantRule.NONE, ProgramSemanticTag.ASSIGNMENT)
            .when(ConstantRule.NONE, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.LIST, ProgramSemanticTag.EMPTY)
            .when(ConstantRule.NONE, ProgramSemanticTag.ARGUMENT, ProgramSemanticTag.LIST, ProgramSemanticTag.SEQUENCE)
            .when(ConstantRule.FORWARD, ProgramSemanticTag.FORWARD)
            .when(ConstantRule.IDENTIFIER, ProgramSemanticTag.IDENTIFIER, ProgramSemanticTag.USE)
            .when(ConstantRule.LEFT_VALUE, ProgramSemanticTag.LEFT_VALUE)
            .when(ConstantRule.LITERAL, ProgramSemanticTag.LITERAL)
            .when(ConstantRule.NULL_LITERAL, ProgramSemanticTag.NULL_LITERAL)
            .when(ConstantRule.PARENTHESIZED, ProgramSemanticTag.PARENTHESIZED)
            .when(ConstantRule.UNARY_MINUS, ProgramSemanticTag.NEGATE)
            .when(ConstantRule.UNARY_NOT, ProgramSemanticTag.LOGICAL_NOT)
            .when(ConstantRule.ARITHMETIC, ProgramSemanticTag.PLUS)
            .when(ConstantRule.ARITHMETIC, ProgramSemanticTag.MINUS)
            .when(ConstantRule.ARITHMETIC, ProgramSemanticTag.MULTIPLY)
            .when(ConstantRule.ARITHMETIC, ProgramSemanticTag.DIVIDE)
            .when(ConstantRule.LOGICAL, ProgramSemanticTag.OR)
            .when(ConstantRule.LOGICAL, ProgramSemanticTag.AND)
            .when(ConstantRule.EQUALITY, ProgramSemanticTag.EQUAL)
            .when(ConstantRule.EQUALITY, ProgramSemanticTag.NOT_EQUAL)
            .when(ConstantRule.RELATION, ProgramSemanticTag.LESS)
            .when(ConstantRule.RELATION, ProgramSemanticTag.LESS_EQUAL)
            .when(ConstantRule.RELATION, ProgramSemanticTag.GREATER)
            .when(ConstantRule.RELATION, ProgramSemanticTag.GREATER_EQUAL)
            .when(
                    ConstantRule.ARGUMENT_VALUE,
                    ProgramSemanticTag.ARGUMENT,
                    ProgramSemanticTag.VALUE,
                    ProgramSemanticTag.FORWARD
            );

    /**
     * 函数功能：转发子节点属性。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue forward(ShiftReduceSemanticContext context, HeadNode head) {
        for (int i = 0; i < head.size(); i++) {
            ConstantValue child = child(context, head, i);
            if (child != null || context.hasConstantValue(head.get(i))) {
                return child;
            }
        }
        return null;
    }

    /**
     * 函数功能：创建字面量常量值。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue literal(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = head.get(0).toToken().getSource();
        SemanticType type = context.literalType(token);
        String lexeme = SourceTokenStringMapping.utf8(token);
        switch (type.getKind()) {
            case BOOLEAN:
                return new ConstantValue(type, Boolean.parseBoolean(lexeme));
            case INT32:
                return new ConstantValue(type, Integer.parseInt(lexeme));
            case FLOAT64:
                return new ConstantValue(type, Double.parseDouble(lexeme));
            default:
                return null;
        }
    }

    /**
     * 函数功能：处理空字面量常量值。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue nullLiteral(ShiftReduceSemanticContext context, HeadNode head) {
        return new ConstantValue(SemanticType.nullLiteral(), null);
    }

    /**
     * 函数功能：计算一元负号常量表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue unaryMinus(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue operand = child(context, head, 1);
        if (operand == null || !operand.getType().isNumericScalar()) {
            return null;
        }
        if (operand.getType().getKind() == SemanticType.Kind.FLOAT64) {
            return new ConstantValue(operand.getType(), -operand.float64());
        }
        return new ConstantValue(operand.getType(), -operand.int32());
    }

    /**
     * 函数功能：计算逻辑非常量表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue unaryNot(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue operand = child(context, head, 1);
        if (operand == null || !operand.getType().isBooleanScalar()) {
            return null;
        }
        return new ConstantValue(operand.getType(), !operand.bool());
    }

    /**
     * 函数功能：计算算术常量表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue arithmetic(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null || !left.getType().isNumericScalar() || !right.getType().isNumericScalar()) {
            return null;
        }
        SemanticType resultType = context.commonBinaryType(left.getType(), right.getType());
        double leftValue = left.getType().getKind() == SemanticType.Kind.FLOAT64 ? left.float64() : left.int32();
        double rightValue = right.getType().getKind() == SemanticType.Kind.FLOAT64 ? right.float64() : right.int32();
        if (head.containsTag(ProgramSemanticTag.PLUS)) {
            return numericResult(resultType, leftValue + rightValue);
        }
        if (head.containsTag(ProgramSemanticTag.MINUS)) {
            return numericResult(resultType, leftValue - rightValue);
        }
        if (head.containsTag(ProgramSemanticTag.MULTIPLY)) {
            return numericResult(resultType, leftValue * rightValue);
        }
        if (head.containsTag(ProgramSemanticTag.DIVIDE)) {
            return numericResult(resultType, leftValue / rightValue);
        }
        throw new CompilerException("unsupported arithmetic operator.");
    }

    /**
     * 函数功能：计算逻辑常量表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue logical(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null || !left.getType().isBooleanScalar() || !right.getType().isBooleanScalar()) {
            return null;
        }
        boolean result =
                head.containsTag(ProgramSemanticTag.OR) ? left.bool() || right.bool() : left.bool() && right.bool();
        return new ConstantValue(SemanticType.scalar(SemanticType.Kind.BOOLEAN), result);
    }

    /**
     * 函数功能：处理相等性表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue equality(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null) {
            return null;
        }
        boolean result;
        if (left.getType().isNumericScalar() && right.getType().isNumericScalar()) {
            double leftValue = left.getType().getKind() == SemanticType.Kind.FLOAT64 ? left.float64() : left.int32();
            double rightValue =
                    right.getType().getKind() == SemanticType.Kind.FLOAT64 ? right.float64() : right.int32();
            result = leftValue == rightValue;
        } else if (left.getType().equals(right.getType())) {
            result = left.getValue().equals(right.getValue());
        } else {
            return null;
        }
        return new ConstantValue(
                SemanticType.scalar(SemanticType.Kind.BOOLEAN),
                head.containsTag(ProgramSemanticTag.EQUAL) == result
        );
    }

    /**
     * 函数功能：处理关系表达式。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue relation(ShiftReduceSemanticContext context, HeadNode head) {
        ConstantValue left = child(context, head, 0);
        ConstantValue right = child(context, head, 2);
        if (left == null || right == null || !left.getType().isNumericScalar() || !right.getType().isNumericScalar()) {
            return null;
        }
        double leftValue = left.getType().getKind() == SemanticType.Kind.FLOAT64 ? left.float64() : left.int32();
        double rightValue = right.getType().getKind() == SemanticType.Kind.FLOAT64 ? right.float64() : right.int32();
        boolean result;
        if (head.containsTag(ProgramSemanticTag.LESS)) {
            result = leftValue < rightValue;
        } else if (head.containsTag(ProgramSemanticTag.LESS_EQUAL)) {
            result = leftValue <= rightValue;
        } else if (head.containsTag(ProgramSemanticTag.GREATER)) {
            result = leftValue > rightValue;
        } else if (head.containsTag(ProgramSemanticTag.GREATER_EQUAL)) {
            result = leftValue >= rightValue;
        } else {
            throw new CompilerException("unsupported relational operator.");
        }
        return new ConstantValue(SemanticType.scalar(SemanticType.Kind.BOOLEAN), result);
    }

    /**
     * 函数功能：计算数值常量结果。
     * 输入：
     * - type：SemanticType 类型参数。
     * - value：double 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue numericResult(SemanticType type, double value) {
        if (type.getKind() == SemanticType.Kind.FLOAT64) {
            return new ConstantValue(type, value);
        }
        return new ConstantValue(type, (int) value);
    }

    /**
     * 函数功能：获取标识符对应的常量值。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue identifierConstant(ShiftReduceSemanticContext context, HeadNode head) {
        SourceToken token = head.get(0).toToken().getSource();
        var record = context.getIdentifier(token);
        return record == null ? null : record.getConstantValue();
    }

    /**
     * 函数功能：获取指定子节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - head：HeadNode 类型参数。
     * - index：int 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    private static ConstantValue child(ShiftReduceSemanticContext context, HeadNode head, int index) {
        return context.getConstantValue(head.get(index));
    }

    /**
     * 函数功能：获取当前规约头节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：HeadNode 类型返回值。
     */
    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }

    /**
     * 函数功能：处理规约事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */
    @Override
    public void onReduce(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        HeadNode head = currentReducedHead(context);
        ConstantValue value = RULES.resolve(production).build(context, head);
        if (value != null) {
            context.bindConstantValue(head, value);
        }
        ShiftReduceCallback.super.onReduce(context, production);
    }

    /**
     * 单个产生式对应的常量构造规则。
     * <p>
     * 作用：
     * <p>
     * ConstantRule 是一个函数式接口。
     * RULES 表根据 production tag 选出具体 ConstantRule，
     * 再调用 build 方法尝试为当前归约节点构造 ConstantValue。
     */
    @FunctionalInterface
    private interface ConstantRule {
        ConstantRule NONE = (context, head) -> null;
        ConstantRule FORWARD = ConstantValueBuildCallback::forward;
        ConstantRule IDENTIFIER = ConstantValueBuildCallback::identifierConstant;
        ConstantRule LEFT_VALUE = (context, head) -> child(context, head, 0);
        ConstantRule LITERAL = ConstantValueBuildCallback::literal;
        ConstantRule NULL_LITERAL = ConstantValueBuildCallback::nullLiteral;
        ConstantRule PARENTHESIZED = (context, head) -> child(context, head, 1);
        ConstantRule UNARY_MINUS = ConstantValueBuildCallback::unaryMinus;
        ConstantRule UNARY_NOT = ConstantValueBuildCallback::unaryNot;
        ConstantRule ARITHMETIC = ConstantValueBuildCallback::arithmetic;
        ConstantRule LOGICAL = ConstantValueBuildCallback::logical;
        ConstantRule EQUALITY = ConstantValueBuildCallback::equality;
        ConstantRule RELATION = ConstantValueBuildCallback::relation;
        ConstantRule ARGUMENT_VALUE = ConstantValueBuildCallback::forward;

        /**
         * 函数功能：构建目标对象。
         * 输入：
         * - context：ShiftReduceSemanticContext 类型参数。
         * - head：HeadNode 类型参数。
         * 输出：ConstantValue 类型返回值。
         */
        ConstantValue build(ShiftReduceSemanticContext context, HeadNode head);
    }
}

