package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:30
 */
public interface GrammarDefineProduction extends GrammarProduction {

    HeadDefineSymbol getDefine();

    @Override
    default HeadSymbol getHead() {
        return getDefine();
    }
}
