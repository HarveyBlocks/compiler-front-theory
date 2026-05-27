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
    private ConstantConditionSupport() {
    }

    /**
     * 读取某个子节点的布尔常量值；无法静态确定时返回 null。
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
     * 判断某个子节点是否已经被折叠为布尔常量。
     */
    static boolean isBooleanConstant(ShiftReduceSemanticContext context, int childIndex) {
        return booleanValue(context, childIndex) != null;
    }
}

