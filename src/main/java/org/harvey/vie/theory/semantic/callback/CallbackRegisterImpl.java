package org.harvey.vie.theory.semantic.callback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-08 13:03
 */
public class CallbackRegisterImpl<T extends SemanticCallback> implements CallbackRegister<T> {
    private final List<T> list = new ArrayList<>();
/**
 * 函数功能：获取元素数量。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    @Override
    public int size() {
        return list.size();
    }
/**
 * 函数功能：获取当前对象的迭代器。
 * 输入：
 * - 无。
 * 输出：Iterator<T> 类型集合或迭代结果。
 */

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
/**
 * 函数功能：添加指定元素。
 * 输入：
 * - callable：T 类型参数。
 * 输出：无。
 */

    @Override
    public void add(T callable) {
        list.add(callable);
    }

}
