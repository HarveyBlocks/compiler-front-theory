package org.harvey.vie.theory.syntax.grammar.produce;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadDefineSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 01:00
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DefineSimpleGrammarProduction implements SimpleGrammarProduction {
    private final HeadDefineSymbol head;
    private final AlterableSymbol body;

    public HeadDefineSymbol getDefine() {
        return head;
    }

    @Override
    public String toString() {
        return head + "->" + body;
    }
}
