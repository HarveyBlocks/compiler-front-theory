package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:45
 */
public interface TerminalSymbol extends ConcatenableSymbol {

    String getValue();
    @Override
    default boolean isConcatenation() {
        return false;
    }
    @Override
    default boolean isTerminal() {
        return true;
    }

    @Override
    default TerminalSymbol toTerminal() {
        return this;
    }
}


