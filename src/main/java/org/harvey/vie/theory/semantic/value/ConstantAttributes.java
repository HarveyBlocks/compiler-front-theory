package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;

/**
 * 读取当前归约节点及其子节点常量属性的工具类。
 *
 * 作用：
 *
 * ConstantAttributes 封装了一个常见访问模式：
 *
 * 1. 从 ShiftReduceSemanticContext 中取得当前刚归约出的 HeadNode。
 * 2. 按子节点下标找到对应语法树节点。
 * 3. 从 ConstantValueContext 中读取常量值或判断是否存在常量属性。
 *
 * 在语法制导翻译阶段，如果某个表达式已经被折叠成编译期常量，
 * 后续命令翻译器就可以直接生成 loadConstant 指令，减少运行期求值代码。
 */
public final class ConstantAttributes {
    /**
     * 函数功能：创建 ConstantAttributes 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private ConstantAttributes() {
    }

    /**
     * 函数功能：获取指定子节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - index：int 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    public static ConstantValue child(ShiftReduceSemanticContext context, int index) {
        ShiftReduceSyntaxTreeNode child = reducedHead(context).get(index);
        return context.getConstantValue(child);
    }

    /**
     * 函数功能：判断子节点是否为常量。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - index：int 类型参数。
     * 输出：判断结果布尔值。
     */
    public static boolean childIsConstant(ShiftReduceSemanticContext context, int index) {
        ShiftReduceSyntaxTreeNode child = reducedHead(context).get(index);
        return context.hasConstantValue(child);
    }

    /**
     * 函数功能：获取处理结果。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：ConstantValue 类型返回值。
     */
    public static ConstantValue result(ShiftReduceSemanticContext context) {
        return context.getConstantValue(reducedHead(context));
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

