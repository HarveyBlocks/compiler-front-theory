package org.harvey.vie.theory.demo.semantic.node;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

/**
 * TODO 叶子
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 18:41
 */
public interface TerminalNode extends GrammarSyntaxTreeNode {
    /**
     * 函数功能：获取终结节点对应的文法终结符号。
     * 输入：
     * - 无。
     * 输出：TerminalSymbol 终结符号。
     */
    TerminalSymbol getSymbol();

    /**
     * 函数功能：获取终结节点对应的源词法单元。
     * 输入：
     * - 无。
     * 输出：SourceToken 源词法单元。
     */
    SourceToken getToken();
}
