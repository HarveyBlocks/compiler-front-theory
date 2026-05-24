package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalFactor;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

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

    /**
     * @param tag 目前最后一个 body 上的 Tag
     */
    GrammarProductionBuilder tagLast(SemanticTag... tag);

    /**
     * @param tag 指定 index 的 body 上的 Tag
     */
    GrammarProductionBuilder tag(int i, SemanticTag... tag);

    GrammarDefineProduction build();


    HeadDefineSymbol getHead();

}
