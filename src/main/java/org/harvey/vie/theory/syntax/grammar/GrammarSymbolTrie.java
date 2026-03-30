package org.harvey.vie.theory.syntax.grammar;

import org.harvey.vie.theory.syntax.grammar.symbol.ConcatenableSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 17:26
 */
public interface GrammarSymbolTrie {
    boolean accept();

    ConcatenableSymbol get();

    ConcatenableSymbol next(String key);
}
