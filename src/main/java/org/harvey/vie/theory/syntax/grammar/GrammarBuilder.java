package org.harvey.vie.theory.syntax.grammar;

import org.harvey.vie.theory.syntax.grammar.produce.GrammarProduction;
import org.harvey.vie.theory.syntax.grammar.produce.ProductionSet;

/**
 * TODO 构造文法
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:47
 */
public interface GrammarBuilder {
    /**
     * 增加一个产生式
     */
    GrammarBuilder addProduction(GrammarProduction production);

    ProductionSet build();
}
