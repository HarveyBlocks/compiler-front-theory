package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 19:36
 */
public class FirstMapBuilder {
    private final ProductionSetContext context;
    private final Map<HeadSymbol, FirstSetBuilder> firstMap;
    private final Set<TerminalSymbol> terminalSet;
/**
 * 函数功能：创建 FirstMapBuilder 对象。
 * 输入：
 * - context：ProductionSetContext 类型参数。
 * 输出：无。
 */

    FirstMapBuilder(ProductionSetContext context) {
        this.context = context;
        firstMap = new HashMap<>();
        terminalSet = new HashSet<>();
    }
/**
 * 函数功能：获取指定非终结符的 FIRST 集构建器。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * 输出：FirstSetBuilder 类型返回值。
 */

    public FirstSetBuilder getBuilder(HeadSymbol head) {
        return firstMap.computeIfAbsent(head, k -> new FirstSetBuilder());
    }
/**
 * 函数功能：获取指定头部符号的候选式集合。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * 输出：GrammarAlternation 类型返回值。
 */

    public GrammarAlternation getAlternation(HeadSymbol head) {
        return context.getAlternation(head);
    }
/**
 * 函数功能：添加连接体中的所有终结符。
 * 输入：
 * - concatenation：GrammarConcatenation 类型参数。
 * 输出：无。
 */

    public void addAllTerminal(GrammarConcatenation concatenation) {
        concatenation.stream()
                .filter(GrammarUnitSymbol::isTerminal)
                .map(GrammarUnitSymbol::toTerminal)
                .forEach(terminalSet::add);
    }
/**
 * 函数功能：构建目标对象。
 * 输入：
 * - 无。
 * 输出：FirstMap 类型返回值。
 */

    public FirstMap build() {
        Map<HeadSymbol, FirstSet> built = firstMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
        return new FirstMapImpl(built, terminalSet);
    }
/**
 * 函数功能：获取终结符集合大小。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */


    public int terminalSetSize() {
        return terminalSet.size();
    }
}
