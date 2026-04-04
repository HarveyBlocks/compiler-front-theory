package org.harvey.vie.theory.syntax.td;

import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.symbol.*;
import org.harvey.vie.theory.syntax.td.tree.node.SyntaxTreeNode;
import org.harvey.vie.theory.syntax.td.conflict.LexicalConflictResolver;
import org.harvey.vie.theory.syntax.td.table.AnalysisTable;

import java.util.Iterator;

/**
 * TODO null for end mark
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-31 23:17
 */
public class PredictiveAnalyzerImpl implements PredictiveAnalyzer {

    private final AnalysisTable analysisTable;

    private final LexicalConflictResolver lexicalConflictResolver;

    public PredictiveAnalyzerImpl(AnalysisTable analysisTable, LexicalConflictResolver lexicalConflictResolver) {
        this.analysisTable = analysisTable;
        this.lexicalConflictResolver = lexicalConflictResolver;
    }

    @Override
    public SyntaxTreeNode analysis(GrammarUnitSymbol start, SourceTokenIterator iterator, ErrorContext errorContext)
            throws CompileException {
        SyntaxAnalysisContext ctx = new SyntaxAnalysisContext(start, iterator, errorContext);
        while (!ctx.isStackEmpty()) {
            GrammarSyntaxTreeNodeBuilder nodeBuilder = ctx.popBuilder();
            GrammarUnitSymbol top = nodeBuilder.getGrammarSymbol();
            if (top == SyntaxAnalysisContext.END_MARK) {
                // 1. 当 X=a=$ 停止分析, 接受, 成功
                if (!iterator.hasNext()) {
                    // 接受, 成功
                    break;
                } else {
                    throw new CompileException("Unexpected token at: " + iterator.next().hintString());
                }
            }
            SourceToken token = ctx.currentToken();
            if (top.isTerminal()) {
                terminal(token, nodeBuilder, ctx);
            } else {
                head(token, nodeBuilder, ctx);
                // 3.4 goto 1
            }
        }
        return ctx.buildTree();
    }

    private void terminal(
            SourceToken token,
            GrammarSyntaxTreeNodeBuilder nodeBuilder,
            SyntaxAnalysisContext ctx) throws CompileException {
        // 2. 当 X is terminal 且 X = a != $, 弹出 X, 前进输入, goto 1
        TerminalSymbol terminal = nodeBuilder.toTerminal();
        if (terminal.match(token)) {
            nodeBuilder.setToken(token);
            ctx.next(); // 消费
        } else {
            // 不匹配
            // 冲突,是否进行修复?
            boolean success = lexicalConflictResolver.resolveTerminalConflict(token, nodeBuilder, ctx);
            if (success) {
                return;
            }
            throw new CompileException("expected: " + terminal.hint());
        }
    }

    private void head(
            SourceToken token,
            GrammarSyntaxTreeNodeBuilder nodeBuilder,
            SyntaxAnalysisContext ctx) throws CompileException {
        // 不是 terminal
        // 3. 当 X not terminal, 查表 M[X,a]
        AlterableSymbol alterableSymbol = analysisTable.get(nodeBuilder.toHead(), token);
        if (alterableSymbol == null) {
            // 冲突,是否进行修复?
            boolean success = lexicalConflictResolver.resolveEmptyProduction(token, nodeBuilder, ctx);
            if (success) {
                return;
            }
            throw new CompileException("Situations that cannot be found in the analysis table.");
        }
        // 3.2 逆序入栈
        if (alterableSymbol == GrammarSymbol.EPSILON) {
            // 表项产生 X -> ε
            nodeBuilder.setChildEpsilon();
        } else if (alterableSymbol.isConcatenation()) {
            // 表项产生 X -> UVW
            GrammarConcatenation concatenation = alterableSymbol.toConcatenation();
            Iterator<GrammarUnitSymbol> iter = concatenation.reverseIterator();
            while (iter.hasNext()) {
                GrammarUnitSymbol next = iter.next();
                GrammarSyntaxTreeNodeBuilder childBuilder = nodeBuilder.buildChild(next);
                ctx.pushBuilder(childBuilder);
            }
        } else {
            throw new IllegalStateException("Grammar definition is wrong, unreasonable type.");
        }
        // 3.3 不消费输入
    }

}
