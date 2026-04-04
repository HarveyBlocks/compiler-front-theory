package org.harvey.vie.theory.syntax.bu.item;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 17:32
 */
@EqualsAndHashCode
@AllArgsConstructor
class ItemSetImpl implements ItemSet {
    private final Set<ProductionItem> set;
    private final Map<TerminalSymbol, Integer> terminalGoto;
    private final Map<HeadSymbol, Integer> headGoto;


    @Override
    public boolean contains(ProductionItem item) {
        return set.contains(item);
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public int gotoUnit(GrammarUnitSymbol unit) {
        return unit.isTerminal() ? terminalGoto.get(unit.toTerminal()) : headGoto.get(unit.toHead());
    }

    @Override
    public Iterator<ProductionItem> iterator() {
        return set.iterator();
    }

    @Override
    public String toString() {
        return set.stream().map(Objects::toString).map(s -> '`' + s + '`').collect(Collectors.joining(", ", "{", "}"));
    }
}
