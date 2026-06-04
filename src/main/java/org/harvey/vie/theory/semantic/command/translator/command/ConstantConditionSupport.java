package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.value.ConstantAttributes;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * 条件表达式常量折叠的辅助工具。
 *
 * @author Temper
 */
final class ConstantConditionSupport {
    /**
     * 函数功能：创建 ConstantConditionSupport 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private ConstantConditionSupport() {
    }

    /**
     * 函数功能：获取布尔常量值。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - childIndex：int 类型参数。
     * 输出：判断结果布尔值。
     */
    static Boolean booleanValue(ShiftReduceSemanticContext context, int childIndex) {
        if (!ConstantAttributes.childIsConstant(context, childIndex)) {
            return null;
        }
        ConstantValue value = ConstantAttributes.child(context, childIndex);
        if (value == null || !value.getType().isBooleanScalar()) {
            return null;
        }
        return value.bool();
    }

    /**
     * 函数功能：判断指定子节点是否为布尔常量。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - childIndex：int 类型参数。
     * 输出：判断结果布尔值。
     */
    static boolean isBooleanConstant(ShiftReduceSemanticContext context, int childIndex) {
        return booleanValue(context, childIndex) != null;
    }
}

