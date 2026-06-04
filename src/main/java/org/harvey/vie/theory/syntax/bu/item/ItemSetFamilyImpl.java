package org.harvey.vie.theory.syntax.bu.item;

import java.util.Arrays;
import java.util.Iterator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 17:29
 */
class ItemSetFamilyImpl implements ItemSetFamily {
    private final ItemSet[] array;
    private final int startIndex;
/**
 * 函数功能：创建 ItemSetFamilyImpl 对象。
 * 输入：
 * - startIndex：int 类型参数。
 * - array：ItemSet[] 类型参数。
 * 输出：无。
 */

    public ItemSetFamilyImpl(int startIndex, ItemSet[] array) {
        this.array = array;
        this.startIndex = startIndex;
    }
/**
 * 函数功能：获取指定位置或键对应的元素。
 * 输入：
 * - i：int 类型参数。
 * 输出：ItemSet 类型返回值。
 */

    @Override
    public ItemSet get(int i) {
        return array[i];
    }
/**
 * 函数功能：获取起始项目集索引。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    @Override
    public int startIndex() {
        return startIndex;
    }
/**
 * 函数功能：获取元素数量。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    @Override
    public int size() {
        return array.length;
    }
/**
 * 函数功能：获取当前对象的迭代器。
 * 输入：
 * - 无。
 * 输出：Iterator<ItemSet> 类型集合或迭代结果。
 */

    @Override
    public Iterator<ItemSet> iterator() {
        return Arrays.stream(array).iterator();
    }
}
