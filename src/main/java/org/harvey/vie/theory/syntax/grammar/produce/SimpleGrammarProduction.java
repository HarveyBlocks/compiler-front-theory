package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

/**
 * TODO 没有 `|` 的产生式
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 00:59
 */
public interface SimpleGrammarProduction {
    HeadSymbol getHead();

    AlterableSymbol getBody();
}
