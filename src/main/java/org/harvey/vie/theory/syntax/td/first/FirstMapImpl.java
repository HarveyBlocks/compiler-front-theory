package org.harvey.vie.theory.syntax.td.first;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.symbol.ConcatenableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 15:04
 */
@AllArgsConstructor
public class FirstMapImpl implements FirstMap {
    private final Map<HeadSymbol, FirstSet> headMap;
    private final Set<TerminalSymbol> terminalSet;

    @Override
    public FirstSet get(HeadSymbol head) {
        return headMap.get(head);
    }

    @Override
    public FirstSet get(TerminalSymbol terminal) {
        return terminalSet.contains(terminal) ? new TerminalFirstSet(terminal) : null;
    }

    @Override
    public Set<TerminalSymbol> terminalSet() {
        return terminalSet;
    }

    @Override
    public FirstSet first(Iterable<ConcatenableSymbol> iterable) {
        Set<TerminalSymbol> allFirstSet = new HashSet<>();
        boolean containsEpsilon = true;
        for (ConcatenableSymbol symbol : iterable) {
            if (symbol.isConcatenation()) {
                throw new IllegalStateException("require non-concatenation grammar symbol here.");
            }
            FirstSet firstSet = symbol.isTerminal() ? get(symbol.toTerminal()) : get(symbol.toHead());
            allFirstSet.addAll(firstSet.firstExceptEpsilon());
            if (!firstSet.containsEpsilon()) {
                containsEpsilon = false;
                break;
            }  // 可空则继续
        }
        return new FirstSetImpl(allFirstSet, containsEpsilon);
    }

    @Override
    public Iterator<Map.Entry<ConcatenableSymbol, FirstSet>> iterator() {
        return new EntryIterator();
    }

    private class EntryIterator implements Iterator<Map.Entry<ConcatenableSymbol, FirstSet>> {
        private final Iterator<Map.Entry<HeadSymbol, FirstSet>> headIter = headMap.entrySet().iterator();
        private final Iterator<TerminalSymbol> terminalIter = terminalSet.iterator();

        @Override
        public boolean hasNext() {
            return headIter.hasNext() || terminalIter.hasNext();
        }

        @Override
        public Map.Entry<ConcatenableSymbol, FirstSet> next() {
            if (headIter.hasNext()) {
                Map.Entry<HeadSymbol, FirstSet> next = headIter.next();
                return Map.entry(next.getKey(), next.getValue());
            }
            TerminalSymbol next = terminalIter.next();
            return Map.entry(next, get(next));
        }
    }
}
