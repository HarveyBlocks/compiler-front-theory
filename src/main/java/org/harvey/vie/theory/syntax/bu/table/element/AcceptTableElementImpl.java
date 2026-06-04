package org.harvey.vie.theory.syntax.bu.table.element;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 23:36
 */
@Getter
@AllArgsConstructor
public class AcceptTableElementImpl implements AcceptTableElement {
    private final int production;

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return "accept " + production;
    }

}
