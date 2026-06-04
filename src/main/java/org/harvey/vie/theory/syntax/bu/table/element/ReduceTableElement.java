package org.harvey.vie.theory.syntax.bu.table.element;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 21:45
 */
public interface ReduceTableElement extends ActiveTableElement {
    /**
     * 函数功能：判断动作表元素是否冲突。
     * 输入：
     * - other：ActiveTableElement 类型参数。
     * 输出：判断结果布尔值。
     */

    @Override
    default boolean conflict(ActiveTableElement other) {
        if (!other.isReduce() || other.isAccept()) {
            return true;
        }
        return !Objects.equals(getProduction(), other.getProduction());
    }

    /**
     * 函数功能：判断是否为移进动作。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    default boolean isShift() {
        return false;
    }

    /**
     * 函数功能：判断是否为规约动作。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    default boolean isReduce() {
        return true;
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
