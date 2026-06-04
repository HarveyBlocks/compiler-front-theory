package org.harvey.vie.theory.syntax.grammar;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.produce.GrammarProduction;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSet;
import org.harvey.vie.theory.syntax.grammar.symbol.AbstractTagGrammarSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.ReferredHeadSymbol;

/**
 * TODO 使用Index对{@link HeadDefineSymbol}进行映射, 变为{@link ReferredHeadSymbol}
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:50
 */
public class IndexReferGrammarBuilderImpl implements GrammarBuilder {
    /**
     * 函数功能：添加语法产生式。
     * 输入：
     * - grammarProduction：GrammarProduction 类型参数。
     * 输出：GrammarBuilder 类型返回值。
     */
    @Override
    public GrammarBuilder addProduction(GrammarProduction grammarProduction) {
        return null;
    }
/**
 * 函数功能：构建目标对象。
 * 输入：
 * - 无。
 * 输出：ProductionSet 类型返回值。
 */

    @Override
    public ProductionSet build() {
        return null;
    }

    @Getter
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    private static class IndexReferredHeadSymbol extends AbstractTagGrammarSymbol implements ReferredHeadSymbol {
        private final int id;
    }
}
