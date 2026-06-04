package org.harvey.vie.theory.demo.semantic.node;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-02 04:23
 */
@AllArgsConstructor
public class TerminalNodeImpl implements TerminalNode {
    private final TerminalSymbol symbol;
    private final SourceToken token;

    /**
     * 函数功能：获取终结节点对应的文法终结符号。
     * 输入：
     * - 无。
     * 输出：TerminalSymbol 终结符号。
     */
    @Override
    public TerminalSymbol getSymbol() {
        return symbol;
    }

    /**
     * 函数功能：获取终结节点对应的源词法单元。
     * 输入：
     * - 无。
     * 输出：SourceToken 源词法单元。
     */
    @Override
    public SourceToken getToken() {
        return token;
    }

    /**
     * 函数功能：获取终结节点的字符串表示。
     * 输入：
     * - 无。
     * 输出：源词法单元提示字符串。
     */
    @Override
    public String toString() {
        return token.hintString();
    }
}
