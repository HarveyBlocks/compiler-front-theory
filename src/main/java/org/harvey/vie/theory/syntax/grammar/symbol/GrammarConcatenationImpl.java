package org.harvey.vie.theory.syntax.grammar.symbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:07
 */
public class GrammarConcatenationImpl extends AbstractTagGrammarSymbol implements GrammarConcatenation {
    private final List<GrammarUnitSymbol> list;
/**
 * 函数功能：创建 GrammarConcatenationImpl 对象。
 * 输入：
 * - 无。
 * 输出：无。
 */

    public GrammarConcatenationImpl() {
        list = new ArrayList<>();
    }
/**
 * 函数功能：创建 GrammarConcatenationImpl 对象。
 * 输入：
 * - array：GrammarUnitSymbol[] 类型参数。
 * 输出：无。
 */

    public GrammarConcatenationImpl(GrammarUnitSymbol[] array) {
        this.list = List.copyOf(List.of(array));
    }
/**
 * 函数功能：连接语法符号。
 * 输入：
 * - concatenable：ConcatenableSymbol 类型参数。
 * 输出：无。
 */

    @Override
    public void concatenate(ConcatenableSymbol concatenable) {
        Objects.requireNonNull(concatenable);
        concatenate0(concatenable);
    }
/**
 * 函数功能：执行底层语法符号连接。
 * 输入：
 * - concatenable：ConcatenableSymbol 类型参数。
 * 输出：无。
 */

    private void concatenate0(ConcatenableSymbol concatenable) {
        if (concatenable.isConcatenable()) {
            if (concatenable.isConcatenation()) {
                for (GrammarUnitSymbol symbol : concatenable.toConcatenation()) {
                    concatenate0(symbol);
                }
                return;
            } else {
                list.add(concatenable.toUnit());
                return;
            }
        }
        throw new IllegalStateException(
                "Unknown type of ConcatenableSymbol concatenateTerminal into the GrammarConcatenation: " +
                concatenable.getClass());
    }
/**
 * 函数功能：获取指定位置或键对应的元素。
 * 输入：
 * - i：int 类型参数。
 * 输出：GrammarUnitSymbol 类型返回值。
 */

    @Override
    public GrammarUnitSymbol get(int i) {
        return list.get(i);
    }
/**
 * 函数功能：获取指定位置开始的列表迭代器。
 * 输入：
 * - index：int 类型参数。
 * 输出：ListIterator<GrammarUnitSymbol> 类型集合或迭代结果。
 */

    @Override
    public ListIterator<GrammarUnitSymbol> listIterator(int index) {
        return list.listIterator(index);
    }
/**
 * 函数功能：获取指定位置开始的列表迭代器。
 * 输入：
 * - 无。
 * 输出：ListIterator<GrammarUnitSymbol> 类型集合或迭代结果。
 */

    @Override
    public ListIterator<GrammarUnitSymbol> listIterator() {
        return list.listIterator();
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
 * 函数功能：获取当前对象的迭代器。
 * 输入：
 * - 无。
 * 输出：Iterator<GrammarUnitSymbol> 类型集合或迭代结果。
 */

    @Override
    public Iterator<GrammarUnitSymbol> iterator() {
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
        return list.stream().map(Object::toString).collect(Collectors.joining(" "));
    }
/**
 * 函数功能：判断当前对象是否与指定对象相等。
 * 输入：
 * - o：Object 类型参数。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GrammarConcatenationImpl)) {
            return false;
        }
        GrammarConcatenationImpl that = (GrammarConcatenationImpl) o;
        return Objects.equals(list, that.list);
    }
/**
 * 函数功能：返回当前对象的哈希值。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }
}
