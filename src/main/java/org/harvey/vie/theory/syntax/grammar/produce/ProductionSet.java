package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.ReferredHeadSymbol;

/**
 * TODO 产生式集合, 也就是文法. 已经被映射了
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:48
 */
public interface ProductionSet extends Iterable<GrammarProduction> {
    ReferredHeadSymbol getHead(int i);

    GrammarSymbol getBody(int i);

    GrammarProduction get(int i);

    int length();

}
