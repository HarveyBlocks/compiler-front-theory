package org.harvey.vie.theory.syntax.grammar.symbol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:07
 */
public class GrammarConcatenationImpl implements GrammarConcatenation {
    private final List<ConcatenableSymbol> list;

    public GrammarConcatenationImpl() {list = new ArrayList<>();}

    @Override
    public void concatenate(ConcatenableSymbol concatenable) {
        Objects.requireNonNull(concatenable);
        concatenate0(concatenable);
    }

    private void concatenate0(ConcatenableSymbol concatenable) {
        if (concatenable instanceof GrammarConcatenation) {
            for (ConcatenableSymbol symbol : ((GrammarConcatenation) concatenable)) {
                concatenate0(symbol);
            }
        } else if (concatenable instanceof TerminalSymbol || concatenable instanceof HeadSymbol) {
            list.add(concatenable);
        } else {
            throw new IllegalStateException(
                    "Unknown type of ConcatenableSymbol concatenateTerminal into the GrammarConcatenation: " +
                    concatenable.getClass());
        }
    }

    @Override
    public ConcatenableSymbol get(int i) {
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
    public Iterator<ConcatenableSymbol> iterator() {
        return list.iterator();
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public String toString() {
        return list.stream().map(Object::toString).collect(Collectors.joining(" "));
    }
}
