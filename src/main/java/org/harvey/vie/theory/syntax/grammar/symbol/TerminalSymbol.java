package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.io.ILoader;
import org.harvey.vie.theory.io.Storage;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-28 00:45
 */
public interface TerminalSymbol extends GrammarUnitSymbol, Storage {
    TerminalSymbol END_MARK_SYMBOL = new EndMarkTerminal();
/**
 * 函数功能：获取终结符因子。
 * 输入：
 * - 无。
 * 输出：TerminalFactor 类型返回值。
 */

    TerminalFactor getFactor();
/**
 * 函数功能：判断当前符号是否为终结符。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    default boolean isTerminal() {
        return true;
    }
/**
 * 函数功能：转换为终结符。
 * 输入：
 * - 无。
 * 输出：TerminalSymbol 类型返回值。
 */

    @Override
    default TerminalSymbol toTerminal() {
        return this;
    }
/**
 * 函数功能：判断是否匹配指定词法单元。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：判断结果布尔值。
 */

    boolean match(SourceToken token);
/**
 * 函数功能：获取提示文本。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    String hint();

    interface Loader<T extends TerminalSymbol> extends ILoader<T> {
    }
}


class EndMarkTerminal implements TerminalSymbol {
    public static final TerminalFactor FACTOR = new Factor();
/**
 * 函数功能：获取终结符因子。
 * 输入：
 * - 无。
 * 输出：TerminalFactor 类型返回值。
 */

    @Override
    public TerminalFactor getFactor() {
        return FACTOR;
    }
/**
 * 函数功能：判断是否匹配指定词法单元。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean match(SourceToken token) {
        return token == SourceTokenIterator.NO_MORE_TOKEN;
    }
/**
 * 函数功能：获取提示文本。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String hint() {
        return "$";
    }
/**
 * 函数功能：返回当前对象的字符串表示。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    @Override
    public String toString() {
        return hint();
    }
/**
 * 函数功能：将对象写入输出流。
 * 输入：
 * - os：OutputStream 类型参数。
 * 输出：整数结果。
 */

    @Override
    public int store(OutputStream os) throws IOException {
        throw new UnsupportedOperationException("Do not store end mark terminal. " +
                                                "Leave it to the outside world to decide " +
                                                "how to persist and better deal with special situations");
    }

    static class Factor implements TerminalFactor {
        /**
         * 函数功能：返回当前对象的字符串表示。
         * 输入：
         * - 无。
         * 输出：字符串结果。
         */
        @Override
        public String toString() {
            return "$";
        }
    }

}
