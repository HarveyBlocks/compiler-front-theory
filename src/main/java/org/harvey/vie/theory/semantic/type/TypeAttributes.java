package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

/**
 * 读取当前归约节点及其子节点类型属性的工具类。
 *
 * 作用：
 *
 * TypeAttributes 封装了一个常见访问模式：
 *
 * 1. 从 ShiftReduceSemanticContext 中取得当前归约出的 HeadNode。
 * 2. 根据子节点下标找到对应语法树节点。
 * 3. 从 TypeContext 中读取 TypeRegister、SemanticType 或定位 token。
 *
 * 这样命令翻译器和其他 callback 不需要重复写这些取值逻辑。
 */
public final class TypeAttributes {
    /**
     * 函数功能：创建 TypeAttributes 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private TypeAttributes() {
    }

    /**
     * 函数功能：获取指定子节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - index：int 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    public static TypeRegister child(ShiftReduceSemanticContext context, int index) {
        ShiftReduceSyntaxTreeNode child = reducedHead(context).get(index);
        TypeRegister register = context.getType(child);
        if (register == null) {
            throw new IllegalStateException("semantic type is absent for child #" + index);
        }
        return register;
    }

    /**
     * 函数功能：获取子节点语义类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - index：int 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public static SemanticType childType(ShiftReduceSemanticContext context, int index) {
        return child(context, index).requireType("semantic type is required for child #" + index + " but the child has no type.");
    }

    /**
     * 函数功能：获取子节点指令类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - index：int 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    public static SemanticType childInstructionType(ShiftReduceSemanticContext context, int index) {
        return child(context, index)
                .requireInstructionType("instruction type is required for child #" + index + " but the child has no instruction type.");
    }

    /**
     * 函数功能：获取子节点锚点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - index：int 类型参数。
     * 输出：SourceToken 类型返回值。
     */
    public static SourceToken childAnchor(ShiftReduceSemanticContext context, int index) {
        return ShiftReduceSyntaxTreeNode.anchor(reducedHead(context).get(index));
    }

    /**
     * 函数功能：获取处理结果。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：TypeRegister 类型返回值。
     */
    public static TypeRegister result(ShiftReduceSemanticContext context) {
        HeadNode head = reducedHead(context);
        TypeRegister register = context.getType(head);
        if (register == null) {
            throw new IllegalStateException("semantic type is absent for current reduced head");
        }
        return register;
    }

    /**
     * 函数功能：判断子节点是否已有语义类型。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - index：int 类型参数。
     * 输出：判断结果布尔值。
     */
    public static boolean childHasType(ShiftReduceSemanticContext context, int index) {
        return context.hasType(reducedHead(context).get(index));
    }

    /**
     * 函数功能：获取结果锚点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：SourceToken 类型返回值。
     */
    public static SourceToken resultAnchor(ShiftReduceSemanticContext context) {
        return ShiftReduceSyntaxTreeNode.anchor(reducedHead(context));
    }

    /**
     * 函数功能：获取当前规约头节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：HeadNode 类型返回值。
     */
    private static HeadNode reducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }
}
