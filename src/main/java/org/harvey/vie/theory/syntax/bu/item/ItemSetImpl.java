package org.harvey.vie.theory.syntax.bu.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 17:32
 */
@AllArgsConstructor
class ItemSetImpl implements ItemSet {
    private final Set<ProductionItem> set;
    @Getter
    private final Map<TerminalSymbol, Integer> terminalGoto;
    @Getter
    private final Map<HeadSymbol, Integer> headGoto;
    private final Map<HeadSymbol, Set<TerminalSymbol>> decisionRules;

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
        Integer set = unit.isTerminal() ? terminalGoto.get(unit.toTerminal()) : headGoto.get(unit.toHead());
        return set == null ? NONE : set;
    }

    @Override
    public Set<TerminalSymbol> decisionRule(HeadSymbol head) {
        Set<TerminalSymbol> set = decisionRules.get(head);
        return set == null ? Collections.emptySet() : set;
    }


    @Override
    public Map<HeadSymbol, Set<TerminalSymbol>> getDecisionRule() {
        return decisionRules;
    }


    @Override
    public Iterator<ProductionItem> iterator() {
        return set.iterator();
    }

    @Override
    public String toString() {
        return set.stream().map(Objects::toString).map(s -> '`' + s + '`').collect(Collectors.joining(", ", "{", "}"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemSetImpl)) {
            return false;
        }
        ItemSetImpl that = (ItemSetImpl) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }
}
