package org.harvey.vie.theory.syntax.grammar.symbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:07
 */
public class GrammarConcatenationImpl implements GrammarConcatenation {
    private final List<GrammarUnitSymbol> list;

    public GrammarConcatenationImpl() {list = new ArrayList<>();}

    @Override
    public void concatenate(ConcatenableSymbol concatenable) {
        Objects.requireNonNull(concatenable);
        concatenate0(concatenable);
    }

    private void concatenate0(ConcatenableSymbol concatenable) {
        if (concatenable.isConcatenable()) {
            if (concatenable.isConcatenation()) {
                for (GrammarUnitSymbol symbol : concatenable.toConcatenation()) {
                    concatenate0(symbol);
                }
                return;
            } else {
                list.add(concatenable.toUnit());
                return;
            }
        }
        throw new IllegalStateException(
                "Unknown type of ConcatenableSymbol concatenateTerminal into the GrammarConcatenation: " +
                concatenable.getClass());
    }

    @Override
    public GrammarUnitSymbol get(int i) {
        return list.get(i);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<GrammarUnitSymbol> iterator() {
        return list.iterator();
    }



    @Override
    public Iterator<GrammarUnitSymbol> reverseIterator() {
        return new ReverseIterator();
    }

    @Override
    public String toString() {
        return list.stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    private class ReverseIterator implements Iterator<GrammarUnitSymbol> {
        private final ListIterator<GrammarUnitSymbol> iter;

        public ReverseIterator() {
            iter = list.listIterator(list.size());
        }

        @Override
        public boolean hasNext() {
            return iter.hasPrevious();
        }

        @Override
        public GrammarUnitSymbol next() {
            return iter.previous();
        }
    }
}
