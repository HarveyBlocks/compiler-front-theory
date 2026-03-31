package org.harvey.vie.theory.syntax.td.first;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.syntax.grammar.symbol.ConcatenableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

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
            return Map.entry(next,get(next));
        }
    }
}
