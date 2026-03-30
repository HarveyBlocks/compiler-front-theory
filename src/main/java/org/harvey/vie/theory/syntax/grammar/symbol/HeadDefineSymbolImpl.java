package org.harvey.vie.theory.syntax.grammar.symbol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:46
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class HeadDefineSymbolImpl implements HeadDefineSymbol {
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
