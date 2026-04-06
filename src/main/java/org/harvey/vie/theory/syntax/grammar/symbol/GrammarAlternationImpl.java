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
 * @date 2026-03-28 02:02
 */
public class GrammarAlternationImpl implements GrammarAlternation {
    private final List<AlterableSymbol> list;
    private boolean alternatedEpsilon;

    public GrammarAlternationImpl() {
        list = new ArrayList<>();
    }

    @Override
    public void alternate(AlterableSymbol symbol) {
        Objects.requireNonNull(symbol);
        if (symbol == GrammarSymbol.EPSILON) {
            if (alternatedEpsilon) {
                return;
            }
            alternatedEpsilon = true;
        }
        list.add(symbol);
    }

    @Override
    public void set(int i, GrammarConcatenation concatenation) {
        Objects.requireNonNull(concatenation);
        if (list.get(i) == GrammarSymbol.EPSILON) {
            alternatedEpsilon = false;
        }
        list.set(i, concatenation);
    }

    @Override
    public AlterableSymbol get(int i) {
        return list.get(i);
    }

    @Override
    public int size() {
        return list.size();
    }


    @Override
    public void alternateEpsilon() {
        alternate(GrammarSymbol.EPSILON);
    }

    @Override
    public boolean alternatedEpsilon() {
        return alternatedEpsilon;
    }

    @Override
    public Iterator<AlterableSymbol> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return list.stream().map(Object::toString).collect(Collectors.joining(" | "));
    }
}
