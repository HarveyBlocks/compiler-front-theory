package org.harvey.vie.theory.syntax.bu.la;

import org.harvey.vie.theory.syntax.bu.item.ProductionItem;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 18:24
 */
public class LookaheadMapImpl implements LookaheadMap {

    private final Map<ProductionItem, Set<TerminalSymbol>> map;

    public LookaheadMapImpl(Map<ProductionItem, Set<TerminalSymbol>> map) {
        this.map = map;
    }

    @Override
    public boolean contains(ProductionItem item, TerminalSymbol terminalSymbol) {
        Set<TerminalSymbol> lookahead = map.get(item);
        if (lookahead == null) {
            return false;
        }
        return lookahead.contains(terminalSymbol);
    }

    @Override
    public Set<TerminalSymbol> get(ProductionItem item) {
        return Optional.ofNullable(map.get(item)).orElseGet(Collections::emptySet);
    }

    @Override
    public String toString() {
        return map.entrySet()
                .stream()
                .map(e -> "`" +
                          e.getKey() +
                          "`: " +
                          e.getValue().stream().map(Objects::toString).collect(Collectors.joining(",", "{", "}")))
                .collect(Collectors.joining(",", "{", "}"));
    }
}
