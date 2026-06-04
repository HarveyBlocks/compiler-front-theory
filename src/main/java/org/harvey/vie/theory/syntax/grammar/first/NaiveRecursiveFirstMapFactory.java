package org.harvey.vie.theory.syntax.grammar.first;

import org.harvey.vie.theory.syntax.grammar.produce.ProductionSetContext;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarConcatenation;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

/**
 * TODO 使用递归计算没有左递归的文法的 FirstSet
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 00:43
 */
public class NaiveRecursiveFirstMapFactory implements FirstMapFactory {
    /**
     * 函数功能：获取 FIRST 集合。
     * 输入：
     * - context：ProductionSetContext 类型参数。
     * 输出：FirstMap 类型返回值。
     */

    @Override
    public FirstMap first(ProductionSetContext context) {
        FirstMapBuilder builder = new FirstMapBuilder(context);
        for (HeadSymbol head : context.headIterable()) {
            first(head, builder);
        }
        return builder.build();
    }

    /**
     * 函数功能：获取 FIRST 集合。
     * 输入：
     * - head：HeadSymbol 类型参数。
     * - mapBuilder：FirstMapBuilder 类型参数。
     * 输出：FirstSetBuilder 类型返回值。
     */

    private FirstSetBuilder first(HeadSymbol head, FirstMapBuilder mapBuilder) {
        FirstSetBuilder builder = mapBuilder.getBuilder(head);
        for (AlterableSymbol symbol : mapBuilder.getAlternation(head)) {
            if (symbol.isEpsilon()) {
                builder.setContainsEpsilon(true);
            } else if (symbol.isConcatenation()) {
                GrammarConcatenation concatenation = symbol.toConcatenation();
                first(concatenation, builder, mapBuilder);
            } else {
                throw new IllegalStateException("Unknown type of: " + symbol.getClass());
            }
        }
        return builder;
    }

    /**
     * 函数功能：获取 FIRST 集合。
     * 输入：
     * - concatenation：GrammarConcatenation 类型参数。
     * - setBuilder：FirstSetBuilder 类型参数。
     * - mapBuilder：FirstMapBuilder 类型参数。
     * 输出：无。
     */

    private void first(GrammarConcatenation concatenation, FirstSetBuilder setBuilder, FirstMapBuilder mapBuilder) {
        if (concatenation.isEmpty()) {
            throw new IllegalStateException("GrammarConcatenation can not be empty!");
        }
        boolean allEpsilon = true;
        for (GrammarUnitSymbol concatenable : concatenation) {
            FirstSetBuilder innerBuilder = innerBuilder(concatenable, mapBuilder);
            setBuilder.addAllExceptEpsilon(innerBuilder);
            if (!innerBuilder.isContainsEpsilon()) {
                allEpsilon = false;
                break;
            }
            // 如果有
            // continue
        }
        setBuilder.setContainsEpsilon(allEpsilon);
        mapBuilder.addAllTerminal(concatenation);
    }

    /**
     * 函数功能：获取语法符号对应的 FIRST 集构建器。
     * 输入：
     * - concatenable：GrammarUnitSymbol 类型参数。
     * - mapBuilder：FirstMapBuilder 类型参数。
     * 输出：FirstSetBuilder 类型返回值。
     */

    private FirstSetBuilder innerBuilder(GrammarUnitSymbol concatenable, FirstMapBuilder mapBuilder) {
        if (concatenable.isTerminal()) {
            return FirstSetBuilder.terminal(concatenable.toTerminal());
        } else {
            return first(concatenable.toHead(), mapBuilder);
        }
    }

}
