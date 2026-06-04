package org.harvey.vie.theory.syntax.td.table;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.grammar.symbol.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 19:00
 */
public interface PredictiveParsingTable {
    int EPSILON_REFERENCE = -1;
    int END_MARK_REFERENCE = 0;
    TerminalSymbol END_MARK_SYMBOL = TerminalSymbol.END_MARK_SYMBOL;
/**
 * 函数功能：获取指定位置或键对应的元素。
 * 输入：
 * - head：HeadSymbol 类型参数。
 * - token：SourceToken 类型参数。
 * 输出：AlterableSymbol 类型返回值。
 */

    AlterableSymbol get(HeadSymbol head, SourceToken token);
/**
 * 函数功能：获取终结符对应的起始语法符号。
 * 输入：
 * - factor：TerminalFactor 类型参数。
 * 输出：GrammarUnitSymbol 类型返回值。
 */

    GrammarUnitSymbol terminalStart(TerminalFactor factor);
/**
 * 函数功能：获取非终结符定义对应的起始语法符号。
 * 输入：
 * - definition：String 类型参数。
 * 输出：GrammarUnitSymbol 类型返回值。
 */

    GrammarUnitSymbol headStart(String definition);
}
