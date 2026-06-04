package org.harvey.vie.theory.syntax.bu.table.element;

import lombok.AllArgsConstructor;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 23:37
 */
@AllArgsConstructor
public class ShiftTableElementImpl implements ShiftTableElement {
    private final int nextStatus;
/**
 * 函数功能：获取移进目标状态。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    @Override
    public int nextStatus() {
        return nextStatus;
    }
/**
 * 函数功能：返回当前对象的字符串表示。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String toString() {
        return "shift " + nextStatus;
    }
}
