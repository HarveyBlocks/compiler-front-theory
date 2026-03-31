package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.GrammarSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:56
 */
public interface ProductionSetContextBuilder {
    GrammarProductionBuilder define(String name);

    TerminalSymbol createTerminal(String value);

    ProductionSetContext build();
}
