package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * 文法中的最基础的部分
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:39
 */
public interface GrammarSymbol {
    AlterableSymbol EPSILON = new EpsilonSymbol();

    static UnsupportedOperationException unsupportedTest() {
        return new UnsupportedOperationException(
                "It is not allowed that invoke the test method form this symbol. Since this symbol is neither of them");
    }

    static UnsupportedOperationException unsupportedCast() {
        return new UnsupportedOperationException(
                "It is not allowed that invoke the method form this symbol. Since this symbol can not cast to the target");
    }

    default boolean isEpsilon() {
        throw unsupportedTest();
    }

    default boolean isConcatenable() {
        throw unsupportedTest();
    }

    default boolean isAlterable() {
        throw unsupportedTest();
    }

    default boolean isConcatenation() {
        throw unsupportedTest();
    }

    default boolean isTerminal() {
        throw unsupportedTest();
    }

    default ConcatenableSymbol toConcatenable() {
        throw unsupportedCast();
    }

    default AlterableSymbol toAlterable() {
        throw unsupportedCast();
    }

    default GrammarConcatenation toConcatenation() {
        throw unsupportedCast();
    }

    default GrammarUnitSymbol toUnit() {
        throw unsupportedCast();
    }

    default TerminalSymbol toTerminal() {
        throw unsupportedCast();
    }

    default HeadSymbol toHead() {
        throw unsupportedCast();
    }
}

class EpsilonSymbol implements GrammarSymbol, AlterableSymbol {
    EpsilonSymbol() {}


    @Override
    public String toString() {
        return "ε";
    }

    @Override
    public boolean isEpsilon() {
        return true;
    }

    @Override
    public boolean isConcatenation() {
        return false;
    }
}

