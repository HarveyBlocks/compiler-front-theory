package org.harvey.vie.theory.syntax.grammar.symbol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:02
 */
public class GrammarAlternationImpl implements GrammarAlternation {
    private final List<AlterableSymbol> list;
    private boolean alternatedEpsilon;

    /**
     * 函数功能：创建 GrammarAlternationImpl 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */

    public GrammarAlternationImpl() {
        list = new ArrayList<>();
    }

    /**
     * 函数功能：添加候选语法符号。
     * 输入：
     * - symbol：AlterableSymbol 类型参数。
     * 输出：无。
     */

    @Override
    public void alternate(AlterableSymbol symbol) {
        Objects.requireNonNull(symbol);
        if (symbol.isEpsilon()) {
            if (alternatedEpsilon) {
                return;
            }
            alternatedEpsilon = true;
        }
        list.add(symbol);
    }

    /**
     * 函数功能：设置指定位置的元素。
     * 输入：
     * - i：int 类型参数。
     * - concatenation：GrammarConcatenation 类型参数。
     * 输出：无。
     */

    @Override
    public void set(int i, GrammarConcatenation concatenation) {
        Objects.requireNonNull(concatenation);
        if (list.get(i).isEpsilon()) {
            alternatedEpsilon = false;
        }
        list.set(i, concatenation);
    }

    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - i：int 类型参数。
     * 输出：AlterableSymbol 类型返回值。
     */

    @Override
    public AlterableSymbol get(int i) {
        return list.get(i);
    }

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
     * 函数功能：添加空串候选产生式。
     * 输入：
     * - 无。
     * 输出：无。
     */


    @Override
    public void alternateEpsilon() {
        alternate(GrammarSymbol.epsilon());
    }

    /**
     * 函数功能：添加空串候选式。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean alternatedEpsilon() {
        return alternatedEpsilon;
    }

    /**
     * 函数功能：获取当前对象的迭代器。
     * 输入：
     * - 无。
     * 输出：Iterator<AlterableSymbol> 类型集合或迭代结果。
     */

    @Override
    public Iterator<AlterableSymbol> iterator() {
        return list.iterator();
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return list.stream().map(Object::toString).collect(Collectors.joining(" | "));
    }
}
