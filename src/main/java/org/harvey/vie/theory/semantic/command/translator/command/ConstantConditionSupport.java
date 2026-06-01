package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.value.ConstantAttributes;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * 控制流支路的条件常量折叠辅助工具。
 * <p>
 * {@link IfStatementTranslator}、{@link IfElseStatementTranslator}、{@link WhileStatementTranslator}
 * 和 {@link DoWhileStatementTranslator} 都会先问这里：条件是否已经能静态确定为 {@code true}/{@code false}。
 * 如果能确定，翻译器可以直接删掉不可达分支或省掉无意义跳转；如果不能确定，才生成
 * {@code if_goto}/{@code ifn_goto} 这类标签跳转命令。
 * <p>
 * 讲完本支路回到对应的控制流翻译器。
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
