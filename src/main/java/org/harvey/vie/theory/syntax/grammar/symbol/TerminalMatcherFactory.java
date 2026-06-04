package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 00:28
 */
public interface TerminalMatcherFactory {
    /**
     * 函数功能：根据输入数据创建目标对象。
     * 输入：
     * - terminalSymbolArray：TerminalSymbol[] 类型参数。
     * 输出：TerminalMatcher 类型返回值。
     */
    TerminalMatcher produce(TerminalSymbol[] terminalSymbolArray);
}
