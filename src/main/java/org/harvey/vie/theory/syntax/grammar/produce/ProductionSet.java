package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.ReferredHeadSymbol;
import org.harvey.vie.theory.util.SimpleList;

/**
 * TODO 产生式集合, 也就是文法. 已经被映射了
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:48
 */
public interface ProductionSet extends SimpleList<GrammarProduction> {
    ReferredHeadSymbol getHead(int i);

    AlterableSymbol getBody(int i);

    GrammarProduction get(int i);

}
