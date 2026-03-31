package org.harvey.vie.theory.syntax.grammar.produce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarAlternation;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:46
 */
@AllArgsConstructor
@Getter
public class GrammarDefineProductionImpl implements GrammarDefineProduction {
    private final HeadDefineSymbol define;
    private final GrammarAlternation body;

    @Override
    public String toString() {
        return define + " -> " + body;
    }
}
