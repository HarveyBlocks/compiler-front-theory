package org.harvey.vie.theory.syntax.bu.table.element;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:45
 */
public interface ActiveTableElement {
    /**
     * 函数功能：判断动作表元素是否冲突。
     * 输入：
     * - other：ActiveTableElement 类型参数。
     * 输出：判断结果布尔值。
     */
    boolean conflict(ActiveTableElement other);

    /**
     * 函数功能：获取指定产生式。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    default int getProduction() {
        throw new UnsupportedOperationException("Do not support to invoke get production by this object");
    }

    /**
     * 函数功能：获取移进目标状态。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */


    default int nextStatus() {
        throw new UnsupportedOperationException("Do not support to invoke get production by this object");
    }

    /**
     * 函数功能：判断是否为移进动作。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean isShift();

    /**
     * 函数功能：判断是否为规约动作。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean isReduce();

    /**
     * 函数功能：判断是否为接受动作。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean isAccept();

    /**
     * 函数功能：处理动作表元素冲突。
     * 输入：
     * - element：ActiveTableElement 类型参数。
     * 输出：无。
     */

    default void dealConflict(ActiveTableElement element) {
        throw new UnsupportedOperationException("Can not deal conflict!");
    }

}
