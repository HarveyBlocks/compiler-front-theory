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
    public ListIterator<GrammarUnitSymbol> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public ListIterator<GrammarUnitSymbol> listIterator() {
        return list.listIterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public Iterator<GrammarUnitSymbol> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return list.stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GrammarConcatenationImpl)) {
            return false;
        }
        GrammarConcatenationImpl that = (GrammarConcatenationImpl) o;
        return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }
}
