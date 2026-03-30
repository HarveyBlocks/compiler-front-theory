package org.harvey.vie.theory.syntax.grammar.symbol;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 02:01
 */
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class TerminalSymbolImpl implements TerminalSymbol {
    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
