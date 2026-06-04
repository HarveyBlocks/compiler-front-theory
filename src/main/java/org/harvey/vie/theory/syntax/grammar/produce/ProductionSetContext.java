package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactory;
import org.harvey.vie.theory.util.SimpleCollection;

/**
 * TODO 上下文, 便于数据重复利用, 例如对于同一个head name definition, 产生同一个对象
 *  * 与 {@link ProductionSet} 的区别在于, {@link ProductionSet} 是成品, 本类是半成品, 是中间过程
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:36
 */
public interface ProductionSetContext extends SimpleCollection<GrammarDefineProduction> {
/**
 * 函数功能：获取指定名称的非终结符定义。
 * 输入：
 * - name：String 类型参数。
 * 输出：HeadDefineSymbol 类型返回值。
 */

    HeadDefineSymbol getDefinition(String name);
/**
 * 函数功能：获取指定对象的索引。
 * 输入：
 * - name：String 类型参数。
 * 输出：整数结果。
 */

    int indexOf(String name);
/**
 * 函数功能：获取指定位置或键对应的元素。
 * 输入：
 * - i：int 类型参数。
 * 输出：GrammarDefineProduction 类型返回值。
 */

    GrammarDefineProduction get(int i);
/**
 * 函数功能：获取指定对象的索引。
 * 输入：
 * - define：HeadDefineSymbol 类型参数。
 * 输出：整数结果。
 */

    Integer indexOf(HeadDefineSymbol define);
/**
 * 函数功能：获取产生式头部符号的可迭代对象。
 * 输入：
 * - 无。
 * 输出：Iterable<HeadSymbol> 类型集合或迭代结果。
 */


    Iterable<HeadSymbol> headIterable();
/**
 * 函数功能：获取指定头部符号的候选式集合。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * 输出：GrammarAlternation 类型返回值。
 */

    GrammarAlternation getAlternation(HeadSymbol head);
/**
 * 函数功能：获取终结符工厂。
 * 输入：
 * - 无。
 * 输出：TerminalFactory 类型返回值。
 */

    TerminalFactory getTerminalFactory();

}
