package org.harvey.vie.theory.syntax.grammar.produce;

import org.harvey.vie.theory.syntax.grammar.symbol.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:15
 */
public class ProductionSetContextBuilderImpl implements ProductionSetContextBuilder {
    private final Map<String, Integer> definitionIdxMap;
    private final List<GrammarProductionBuilder> list;
    private final Map<String, TerminalSymbol> terminalMap;

    public ProductionSetContextBuilderImpl() {
        this.list = new ArrayList<>();
        this.definitionIdxMap = new HashMap<>();
        this.terminalMap = new HashMap<>();
    }

    @Override
    public GrammarProductionBuilder define(String name) {
        Integer idx = definitionIdxMap.computeIfAbsent(name, k -> {
            int i = list.size();
            list.add(new GrammarProductionBuilderImpl(new HeadDefineSymbolImpl(name), this));
            return i;
        });
        return list.get(idx);
    }

    @Override
    public TerminalSymbol createTerminal(String value) {
        return terminalMap.computeIfAbsent(value, TerminalSymbolImpl::new);
    }

    @Override
    public ProductionSetContext build() {
        GrammarDefineProduction[] productions = list.stream()
                .map(GrammarProductionBuilder::build)
                .toArray(GrammarDefineProduction[]::new);
        return new ProductionSetContextImpl(definitionIdxMap, productions);
    }

    @Override
    public GrammarSymbol epsilon() {
        return GrammarSymbol.EPSILON;
    }


}
