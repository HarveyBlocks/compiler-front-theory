package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.td.tree.node.SyntaxTreeNode;

/**
 * TODO 预测分析
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:16
 */
public interface PredictiveAnalyzer {
    SyntaxTreeNode analysis(GrammarUnitSymbol start, SourceTokenIterator iterator, ErrorContext errorContext) throws CompileException;
}
