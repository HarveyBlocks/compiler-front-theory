package org.harvey.vie.theory.syntax.grammar;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.harvey.vie.theory.syntax.grammar.produce.GrammarProduction;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSet;
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
    @Override
    public GrammarBuilder addProduction(GrammarProduction grammarProduction) {
        return null;
    }

    @Override
    public ProductionSet build() {
        return null;
    }

    @AllArgsConstructor
    @Data
    private static class IndexReferredHeadSymbol implements ReferredHeadSymbol {
        private final int id;
    }
}
