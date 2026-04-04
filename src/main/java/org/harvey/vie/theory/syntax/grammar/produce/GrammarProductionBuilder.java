package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactor;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:54
 */
public interface GrammarProductionBuilder {

    GrammarProductionBuilder alternateTerminal(TerminalFactor factor);


    GrammarProductionBuilder alternateDefinition(String definition);


    GrammarProductionBuilder alternateEpsilon();

    GrammarProductionBuilder alternateSelf();

    GrammarProductionBuilder alternatePlaceholder();

    GrammarProductionBuilder concatenateTerminalLast(TerminalFactor factor);

    GrammarProductionBuilder concatenateDefinitionLast(String definition);

    GrammarProductionBuilder concatenateSelfLast();


    GrammarProductionBuilder concatenateTerminal(int i, TerminalFactor factor);


    GrammarProductionBuilder concatenateDefinition(int i, String definition);

    GrammarProductionBuilder concatenateSelf(int i);

    GrammarDefineProduction build();


    HeadDefineSymbol getHead();

}
