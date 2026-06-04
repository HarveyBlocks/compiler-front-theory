package org.harvey.vie.theory.syntax.td.conflict;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO 把一个token进行分割
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 22:34
 */
public interface ConflictTokenSplitter {
    /**
     * 函数功能：拆分冲突词法单元。
     * 输入：
     * - terminalSymbol：TerminalSymbol 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：SourceTokenIterator 类型返回值。
     */
    SourceTokenIterator split(TerminalSymbol terminalSymbol, SourceToken token);
}
