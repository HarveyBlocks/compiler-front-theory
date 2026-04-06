package org.harvey.vie.theory.syntax.td.conflict;

import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.GrammarSyntaxTreeNodeBuilder;
import org.harvey.vie.theory.syntax.td.SyntaxParsingContext;

/**
 * TODO 由于词法分析采用最长匹配, 因此解决冲突的方法是切割
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 22:25
 */
public interface LexicalConflictResolver {
    LexicalConflictResolver PASSIVE = new LexicalConflictResolver() {
    };

    /**
     * @return 不解析直接全部ERROR
     */
    static LexicalConflictResolver passive() {
        return PASSIVE;
    }

    /**
     * @param terminal 期望的terminal
     * @param old      current 的 token 发生了冲突
     */
    @Deprecated
    default SourceTokenIterator resolve(TerminalSymbol terminal, SourceTokenIterator old) throws CompileException {
        throw new UnsupportedOperationException("Can not resolve");
    }

    /**
     * 处理分析表里没有产生式的情况
     *
     * @param token       和 {@link SyntaxParsingContext#currentToken()}的结果完全一致
     * @param nodeBuilder 从 {@link SyntaxParsingContext#popBuilder()} 而来, 已经不在栈中
     */
    default boolean resolveEmptyProduction(
            SourceToken token, GrammarSyntaxTreeNodeBuilder nodeBuilder, SyntaxParsingContext ctx) {
        return false;
    }

    /**
     * 处理需要的terminal和token无法匹配的情况
     *
     * @param token       和 {@link SyntaxParsingContext#currentToken()}的结果完全一致
     * @param nodeBuilder 从 {@link SyntaxParsingContext#popBuilder()} 而来, 已经不在栈中
     */
    default boolean resolveTerminalConflict(
            SourceToken token, GrammarSyntaxTreeNodeBuilder nodeBuilder, SyntaxParsingContext ctx) {
        return false;
    }
}
