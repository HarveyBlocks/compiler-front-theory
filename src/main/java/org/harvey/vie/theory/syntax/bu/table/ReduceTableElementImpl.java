package org.harvey.vie.theory.syntax.bu.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.syntax.grammar.symbol.AlterableSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-06 23:36
 */
@Getter
@AllArgsConstructor
public class ReduceTableElementImpl implements AcceptTableElement {
    private final HeadSymbol head;
    private final AlterableSymbol body;


    @Override
    public String toString() {
        return "reduce " + head.toString() + "->" + body;
    }


}
