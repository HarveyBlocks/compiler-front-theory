package org.harvey.vie.theory.syntax.td;

import lombok.Getter;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.SyntaxErrorMessage;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.tree.node.SyntaxTreeNode;
import org.harvey.vie.theory.syntax.td.table.AnalysisTable;

import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 13:13
 */
@Getter
public class SyntaxAnalysisContext {
    public static final TerminalSymbol END_MARK = AnalysisTable.END_MARK_SYMBOL;
    private final Stack<GrammarSyntaxTreeNodeBuilder> stack;
    private final SourceTokenIterator iterator;
    private final ErrorContext errorContext;
    private final GrammarSyntaxTreeNodeBuilder root;

    public SyntaxAnalysisContext(GrammarUnitSymbol start, SourceTokenIterator iterator, ErrorContext errorContext) {
        this.iterator = iterator;
        this.errorContext = errorContext;
        this.stack = new Stack<>();
        this.root = new GrammarSyntaxTreeNodeBuilder(END_MARK);
        stack.push(root);
        stack.push(root.buildChild(start));
    }

    public SourceToken currentToken() {
        SourceToken token;
        while (true) {
            try {
                token = iterator.current();
                break;
            } catch (CompileException e) {
                // 未完成的token
                errorContext.addError(new SyntaxErrorMessage(iterator.getOffset(), e.getMessage()));
                // 跳过
            }
        }
        return token;
    }

    public void next() {
        // 消费
        try {
            iterator.next();
        } catch (CompileException e) {
            throw new CompilerException(
                    "current mechanism fails. If current must be executed before this next, then this next must not fail!  ",
                    e
            );
        }
    }

    public boolean isStackEmpty() {
        return stack.isEmpty();
    }

    public GrammarSyntaxTreeNodeBuilder popBuilder() {
        return stack.pop();
    }

    public void pushBuilder(GrammarSyntaxTreeNodeBuilder builder) {
        stack.push(builder);
    }

    public GrammarSyntaxTreeNodeBuilder topBuilder() {
        return stack.peek();
    }

    public SyntaxTreeNode buildTree() {
        if (!stack.isEmpty()) {
            throw new IllegalStateException("In the case of impossible, the stack must be empty");
        }
        return root.build();
    }

}
