package org.harvey.vie.theory.syntax.grammar.symbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:59
 */
public interface TerminalFactory {
    /**
     * 函数功能：创建目标对象。
     * 输入：
     * - terminal：TerminalFactor 类型参数。
     * 输出：TerminalSymbol 类型返回值。
     */
    TerminalSymbol create(TerminalFactor terminal);
}
