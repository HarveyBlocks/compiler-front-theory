package org.harvey.vie.theory.syntax.grammar.produce;

import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:37
 */
public class ProductionSetContextImpl implements ProductionSetContext {
    private final Map<String, Integer> definitionIdxMap;
    private final GrammarDefineProduction[] productions;
    @Getter
    private final TerminalFactory terminalFactory;

    /**
     * 函数功能：创建 ProductionSetContextImpl 对象。
     * 输入：
     * - terminalFactory：TerminalFactory 类型参数。
     * - definitionIdxMap：Map<String, Integer> 类型参数。
     * - productions：GrammarDefineProduction[] 类型参数。
     * 输出：无。
     */

    public ProductionSetContextImpl(
            TerminalFactory terminalFactory,
            Map<String, Integer> definitionIdxMap,
            GrammarDefineProduction[] productions) {
        this.terminalFactory = terminalFactory;
        this.definitionIdxMap = definitionIdxMap;
        this.productions = productions;
    }

    /**
     * 函数功能：获取指定名称的非终结符定义。
     * 输入：
     * - name：String 类型参数。
     * 输出：HeadDefineSymbol 类型返回值。
     */

    @Override
    public HeadDefineSymbol getDefinition(String name) {
        return productions[indexOf(name)].getDefine();
    }

    /**
     * 函数功能：获取指定对象的索引。
     * 输入：
     * - name：String 类型参数。
     * 输出：整数结果。
     */

    @Override
    public int indexOf(String name) {
        Integer index = definitionIdxMap.get(name);
        if (index == null) {
            throw new IllegalStateException("Can not found definition of: " + name);
        }
        return index;
    }

    /**
     * 函数功能：获取指定位置或键对应的元素。
     * 输入：
     * - i：int 类型参数。
     * 输出：GrammarDefineProduction 类型返回值。
     */

    @Override
    public GrammarDefineProduction get(int i) {
        return productions[i];
    }

    /**
     * 函数功能：获取指定对象的索引。
     * 输入：
     * - define：HeadDefineSymbol 类型参数。
     * 输出：整数结果。
     */

    @Override
    public Integer indexOf(HeadDefineSymbol define) {
        return indexOf(define.getName());
    }

    /**
     * 函数功能：获取元素数量。
     * 输入：
     * - 无。
     * 输出：整数结果。
     */

    @Override
    public int size() {
        return productions.length;
    }

    /**
     * 函数功能：获取产生式头部符号的可迭代对象。
     * 输入：
     * - 无。
     * 输出：Iterable<HeadSymbol> 类型集合或迭代结果。
     */

    @Override
    public Iterable<HeadSymbol> headIterable() {
        return HeadIterator::new;
    }

    /**
     * 函数功能：获取指定头部符号的候选式集合。
     * 输入：
     * - head：HeadSymbol 类型参数。
     * 输出：GrammarAlternation 类型返回值。
     */

    @Override
    public GrammarAlternation getAlternation(HeadSymbol head) {
        if (!head.isDefine()) {
            throw new IllegalStateException("The head of production is not define head symbol!");
        }
        HeadDefineSymbol define = head.toDefine();
        Integer index = indexOf(define);
        if (index == null) {
            throw new IllegalStateException("Can not found define from context!");
        }
        return get(index).getBody();
    }

    /**
     * 函数功能：获取当前对象的迭代器。
     * 输入：
     * - 无。
     * 输出：Iterator<GrammarDefineProduction> 类型集合或迭代结果。
     */


    @Override
    public Iterator<GrammarDefineProduction> iterator() {
        return Arrays.stream(productions).iterator();
    }

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return Arrays.stream(productions).map(Object::toString).collect(Collectors.joining("\n"));
    }

    private class HeadIterator implements Iterator<HeadSymbol> {
        private final Iterator<Map.Entry<String, Integer>> it = definitionIdxMap.entrySet().iterator();

        /**
         * 函数功能：判断是否存在下一个元素。
         * 输入：
         * - 无。
         * 输出：判断结果布尔值。
         */

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        /**
         * 函数功能：获取下一个元素。
         * 输入：
         * - 无。
         * 输出：HeadSymbol 类型返回值。
         */

        @Override
        public HeadSymbol next() {
            return productions[it.next().getValue()].getHead();
        }
    }
}
