package org.harvey.vie.theory.syntax.grammar.symbol;

import java.util.Iterator;
import java.util.List;

/**
 * TODO 产生式中的连接
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:49
 */
public interface GrammarConcatenation extends ConcatenableSymbol, Iterable<ConcatenableSymbol> {

    void concatenate(ConcatenableSymbol concatenable);

    ConcatenableSymbol get(int i);

    int size();

    boolean isEmpty();
}

