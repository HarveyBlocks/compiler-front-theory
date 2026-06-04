package org.harvey.vie.theory.syntax.td.conflict;

import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.semantic.context.PredictiveSemanticContext;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.GrammarSyntaxTreeNodeBuilder;

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
     * 函数功能：创建被动词法冲突解析器。
     * 输入：
     * - 无。
     * 输出：LexicalConflictResolver 类型返回值。
     */

    static LexicalConflictResolver passive() {
        return PASSIVE;
    }

    /**
     * 函数功能：解析词法冲突并返回词法单元迭代器。
     * 输入：
     * - terminal：TerminalSymbol 类型参数。
     * - old：SourceTokenIterator 类型参数。
     * 输出：SourceTokenIterator 类型返回值。
     */

    @Deprecated
    default SourceTokenIterator resolve(TerminalSymbol terminal, SourceTokenIterator old) throws CompileException {
        throw new UnsupportedOperationException("Can not resolve");
    }

    /**
     * 函数功能：处理空产生式冲突。
     * 输入：
     * - token：SourceToken 类型参数。
     * - nodeBuilder：GrammarSyntaxTreeNodeBuilder 类型参数。
     * - ctx：PredictiveSemanticContext 类型参数。
     * 输出：判断结果布尔值。
     */

    default boolean resolveEmptyProduction(
            SourceToken token, GrammarSyntaxTreeNodeBuilder nodeBuilder, PredictiveSemanticContext ctx) {
        return false;
    }

    /**
     * 函数功能：处理终结符冲突。
     * 输入：
     * - token：SourceToken 类型参数。
     * - nodeBuilder：GrammarSyntaxTreeNodeBuilder 类型参数。
     * - ctx：PredictiveSemanticContext 类型参数。
     * 输出：判断结果布尔值。
     */

    default boolean resolveTerminalConflict(
            SourceToken token, GrammarSyntaxTreeNodeBuilder nodeBuilder, PredictiveSemanticContext ctx) {
        return false;
    }
}
