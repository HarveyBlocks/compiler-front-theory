package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.produce.GrammarDefineProduction;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

/**
 * TODO 采用不动点算法, 求 First 的算法, 不要求输入是消除了左递归的
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 18:26
 */
public class IterativeFixedPointFirstMapFactory implements FirstMapFactory {
    /**
     * 函数功能：添加连接体中的所有终结符。
     * 输入：
     * - context：ProductionSetContext 类型参数。
     * - mapBuilder：FirstMapBuilder 类型参数。
     * 输出：无。
     */
    private static void addAllTerminal(ProductionSetContext context, FirstMapBuilder mapBuilder) {
        context.stream()
                .map(GrammarDefineProduction::getBody)
                .flatMap(GrammarAlternation::stream)
                .filter(AlterableSymbol::isConcatenation)
                .map(GrammarSymbol::toConcatenation)
                .forEach(mapBuilder::addAllTerminal);
    }
/**
 * 函数功能：获取 FIRST 集合。
 * 输入：
 * - production：GrammarDefineProduction 类型参数。
 * - mapBuilder：FirstMapBuilder 类型参数。
 * 输出：判断结果布尔值。
 */

    private static boolean first(GrammarDefineProduction production, FirstMapBuilder mapBuilder) {
        boolean changed = false;
        HeadSymbol head = production.getHead();
        FirstSetBuilder setBuilder = mapBuilder.getBuilder(head);
        GrammarAlternation body = production.getBody();
        for (AlterableSymbol symbol : body) {
            changed = first(symbol, setBuilder, mapBuilder) || changed;
        }
        return changed;
    }
/**
 * 函数功能：获取 FIRST 集合。
 * 输入：
 * - symbol：AlterableSymbol 类型参数。
 * - setBuilder：FirstSetBuilder 类型参数。
 * - mapBuilder：FirstMapBuilder 类型参数。
 * 输出：判断结果布尔值。
 */

    private static boolean first(AlterableSymbol symbol, FirstSetBuilder setBuilder, FirstMapBuilder mapBuilder) {
        if (symbol.isEpsilon()) {
            // 空
            return setEpsilon(setBuilder);
        } else if (symbol.isConcatenation()) {
            return first(symbol.toConcatenation(), setBuilder, mapBuilder);
        }
        throw new IllegalStateException("Unknown type of: " + symbol.getClass() + " in building first set.");
    }
/**
 * 函数功能：获取 FIRST 集合。
 * 输入：
 * - concatenation：GrammarConcatenation 类型参数。
 * - setBuilder：FirstSetBuilder 类型参数。
 * - mapBuilder：FirstMapBuilder 类型参数。
 * 输出：判断结果布尔值。
 */

    private static boolean first(
            GrammarConcatenation concatenation, FirstSetBuilder setBuilder, FirstMapBuilder mapBuilder) {
        boolean changed = false;
        for (GrammarUnitSymbol unitSymbol : concatenation) {
            FirstSetBuilder innerSetBuilder = innerSetBuilder(unitSymbol, mapBuilder);
            changed = addAllExceptEpsilon(setBuilder, innerSetBuilder) || changed;
            if (!innerSetBuilder.isContainsEpsilon()) {
                break; // 终止此产生式
            } else {
                changed = setEpsilon(setBuilder) || changed;
            }
        }

        return changed;
    }
/**
 * 函数功能：添加除空串外的 FIRST 集元素。
 * 输入：
 * - setBuilder：FirstSetBuilder 类型参数。
 * - innerSetBuilder：FirstSetBuilder 类型参数。
 * 输出：判断结果布尔值。
 */

    private static boolean addAllExceptEpsilon(FirstSetBuilder setBuilder, FirstSetBuilder innerSetBuilder) {
        int oldSetSize = setBuilder.setSize();
        setBuilder.addAllExceptEpsilon(innerSetBuilder);
        return oldSetSize != setBuilder.setSize();
    }
/**
 * 函数功能：设置 FIRST 集包含空串。
 * 输入：
 * - setBuilder：FirstSetBuilder 类型参数。
 * 输出：判断结果布尔值。
 */

    private static boolean setEpsilon(FirstSetBuilder setBuilder) {
        boolean oldContains = setBuilder.isContainsEpsilon();
        setBuilder.setContainsEpsilon(true);
        return oldContains != setBuilder.isContainsEpsilon();
    }
/**
 * 函数功能：获取语法符号对应的 FIRST 集构建器。
 * 输入：
 * - unitSymbol：GrammarUnitSymbol 类型参数。
 * - mapBuilder：FirstMapBuilder 类型参数。
 * 输出：FirstSetBuilder 类型返回值。
 */

    private static FirstSetBuilder innerSetBuilder(GrammarUnitSymbol unitSymbol, FirstMapBuilder mapBuilder) {
        if (unitSymbol.isTerminal()) {
            return FirstSetBuilder.terminal(unitSymbol.toTerminal());
        } else {
            return mapBuilder.getBuilder(unitSymbol.toHead());
        }
    }
/**
 * 函数功能：获取 FIRST 集合。
 * 输入：
 * - context：ProductionSetContext 类型参数。
 * 输出：FirstMap 类型返回值。
 */

    @Override
    public FirstMap first(ProductionSetContext context) {
        FirstMapBuilder mapBuilder = new FirstMapBuilder(context);
        addAllTerminal(context, mapBuilder);
        boolean changed;
        do {
            changed = false;
            for (int i = context.size() - 1; i >= 0; i--) {
                changed = first(context.get(i), mapBuilder) || changed;
            }
        } while (changed);
        return mapBuilder.build();
    }
}
