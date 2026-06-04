package org.harvey.vie.theory.syntax.bu.item;

import org.harvey.vie.theory.util.SimpleCollection;

/**
 * TODO 项集族
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 22:13
 */
public interface ItemSetFamily extends SimpleCollection<ItemSet> {
    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - i：int 类型参数。
     * 输出：ItemSet 类型返回值。
     */
    ItemSet get(int i);

    /**
     * 函数功能：获取起始项目集索引。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    int startIndex();

}
