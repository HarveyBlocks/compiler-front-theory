package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactor;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:56
 */
public interface ProductionSetContextBuilder {
    GrammarProductionBuilder define(String name, SemanticTag... tags);

    TerminalSymbol createTerminal(TerminalFactor factor);

    ProductionSetContext build();
}
