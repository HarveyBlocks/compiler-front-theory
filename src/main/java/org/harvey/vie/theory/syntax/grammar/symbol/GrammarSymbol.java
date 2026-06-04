package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * 文法中的最基础的部分
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:39
 */
public interface GrammarSymbol {
    /**
     * 函数功能：获取空串语法符号。
     * 输入：
     * - 无。
     * 输出：AlterableSymbol 类型返回值。
     */
    static AlterableSymbol epsilon() {
        return new EpsilonSymbol();
    }

    /**
     * 函数功能：返回不支持的判断结果。
     * 输入：
     * - 无。
     * 输出：UnsupportedOperationException 类型返回值。
     */

    static UnsupportedOperationException unsupportedTest() {
        return new UnsupportedOperationException(
                "It is not allowed that invoke the test method form this symbol. Since this symbol is neither of them");
    }

    /**
     * 函数功能：执行不支持的类型转换。
     * 输入：
     * - 无。
     * 输出：UnsupportedOperationException 类型返回值。
     */

    static UnsupportedOperationException unsupportedCast() {
        return new UnsupportedOperationException(
                "It is not allowed that invoke the method form this symbol. Since this symbol can not cast to the target");
    }

    /**
     * 函数功能：判断当前产生式项目是否为空串。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isEpsilon() {
        throw unsupportedTest();
    }

    /**
     * 函数功能：判断当前符号是否可连接。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isConcatenable() {
        throw unsupportedTest();
    }

    /**
     * 函数功能：判断当前符号是否可作为候选式。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isAlterable() {
        throw unsupportedTest();
    }

    /**
     * 函数功能：判断当前符号是否为连接体。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isConcatenation() {
        throw unsupportedTest();
    }

    /**
     * 函数功能：判断当前符号是否为终结符。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isTerminal() {
        throw unsupportedTest();
    }

    /**
     * 函数功能：转换为可连接语法符号。
     * 输入：
     * - 无。
     * 输出：ConcatenableSymbol 类型返回值。
     */

    default ConcatenableSymbol toConcatenable() {
        throw unsupportedCast();
    }

    /**
     * 函数功能：转换为可候选语法符号。
     * 输入：
     * - 无。
     * 输出：AlterableSymbol 类型返回值。
     */

    default AlterableSymbol toAlterable() {
        throw unsupportedCast();
    }

    /**
     * 函数功能：转换为语法符号连接体。
     * 输入：
     * - 无。
     * 输出：GrammarConcatenation 类型返回值。
     */

    default GrammarConcatenation toConcatenation() {
        throw unsupportedCast();
    }

    /**
     * 函数功能：转换为语法单元符号。
     * 输入：
     * - 无。
     * 输出：GrammarUnitSymbol 类型返回值。
     */

    default GrammarUnitSymbol toUnit() {
        throw unsupportedCast();
    }

    /**
     * 函数功能：转换为终结符。
     * 输入：
     * - 无。
     * 输出：TerminalSymbol 类型返回值。
     */

    default TerminalSymbol toTerminal() {
        throw unsupportedCast();
    }

    /**
     * 函数功能：转换为非终结符。
     * 输入：
     * - 无。
     * 输出：HeadSymbol 类型返回值。
     */

    default HeadSymbol toHead() {
        throw unsupportedCast();
    }


}

class EpsilonSymbol extends AbstractTagGrammarSymbol implements AlterableSymbol {
    /**
     * 函数功能：创建 EpsilonSymbol 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    EpsilonSymbol() {}

    /**
     * 函数功能：返回当前对象的字符串表示。
     * 输入：
     * - 无。
     * 输出：字符串结果。
     */

    @Override
    public String toString() {
        return "ε";
    }

    /**
     * 函数功能：判断当前产生式项目是否为空串。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean isEpsilon() {
        return true;
    }

    /**
     * 函数功能：判断当前符号是否为连接体。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    @Override
    public boolean isConcatenation() {
        return false;
    }
}
