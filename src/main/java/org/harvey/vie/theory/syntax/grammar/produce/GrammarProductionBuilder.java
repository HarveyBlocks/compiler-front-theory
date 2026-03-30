package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:54
 */
public interface GrammarProductionBuilder {

    GrammarProductionBuilder alternateTerminal(String terminal);


    GrammarProductionBuilder alternateDefinition(String definition);


    GrammarProductionBuilder alternateEpsilon();

    GrammarProductionBuilder alternateSelf();
    GrammarProductionBuilder alternatePlaceholder();

    GrammarProductionBuilder concatenateTerminalLast(String terminal);

    GrammarProductionBuilder concatenateDefinitionLast(String definition);

    GrammarProductionBuilder concatenateSelfLast();


    GrammarProductionBuilder concatenateTerminal(int i, String terminal);


    GrammarProductionBuilder concatenateDefinition(int i, String definition);

    GrammarProductionBuilder concatenateSelf(int i);

    GrammarDefineProduction build();


    HeadDefineSymbol getHead();

}
