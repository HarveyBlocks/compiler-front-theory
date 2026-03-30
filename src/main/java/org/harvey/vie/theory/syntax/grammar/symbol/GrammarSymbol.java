package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * 文法中的最基础的部分
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:39
 */
public interface GrammarSymbol {
    GrammarSymbol EPSILON = new EpsilonSymbol();
    boolean isTerminal();
}
class EpsilonSymbol implements GrammarSymbol {
    EpsilonSymbol() {}


    @Override
    public String toString() {
        return "ε";
    }

    @Override
    public boolean isTerminal() {
        throw new UnsupportedOperationException(
                "It is not allowed that invoke is terminal form epsilon symbol. Epsilon is neither of them");
    }
}

