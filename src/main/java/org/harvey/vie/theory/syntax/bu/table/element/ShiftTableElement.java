package org.harvey.vie.theory.syntax.bu.table.element;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:45
 */
public interface ShiftTableElement extends ActiveTableElement {
    /**
     * 函数功能：获取移进目标状态。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */
    int nextStatus();
    /**
     * 函数功能：判断动作表元素是否冲突。
     * 输入：
     * - other：ActiveTableElement 类型参数。
     * 输出：判断结果布尔值。
     */
    @Override
    default boolean conflict(ActiveTableElement other) {
        if (!other.isShift()) {
            return true;
        } else if (other.nextStatus() != nextStatus()) {
            throw new IllegalStateException(
                    "For the same state I and the same terminal a, the result of GOTO(I, a) is uniquely determined, " +
                    "and thus it cannot be assigned two different shift targets.");
        }
        return false;
    }
/**
 * 函数功能：判断是否为移进动作。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    default boolean isShift() {
        return true;
    }
/**
 * 函数功能：判断是否为规约动作。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    default boolean isReduce() {
        return false;
    }
/**
 * 函数功能：判断是否为接受动作。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    default boolean isAccept() {
        return false;
    }
}
