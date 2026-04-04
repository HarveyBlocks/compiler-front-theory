package org.harvey.vie.theory.syntax.td.conflict;

import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;

import java.util.Stack;

/**
 * TODO 分割器
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-01 22:31
 */
public class SplitTokenResolver implements LexicalConflictResolver {
    private final ConflictTokenSplitter splitter;

    public SplitTokenResolver(ConflictTokenSplitter splitter) {this.splitter = splitter;}


    @Override
    @Deprecated
    public SourceTokenIterator resolve(TerminalSymbol terminal, SourceTokenIterator old) throws CompileException {
        SourceToken current = old.next();// 消费
        SolvedIterator solvedIterator = old instanceof SolvedIterator ? (SolvedIterator) old : new SolvedIterator(old);
        solvedIterator.stack.push(splitter.split(terminal, current));
        return solvedIterator;
    }

    static class SolvedIterator implements SourceTokenIterator {
        private final Stack<SourceTokenIterator> stack;

        SolvedIterator(SourceTokenIterator origin) {
            this.stack = new Stack<>();
            stack.push(origin);
        }

        @Override
        public boolean hasNext() {
            while (!stack.isEmpty()) {
                if (stack.peek().hasNext()) {
                    return true;
                }
                try {
                    stack.pop().close();
                } catch (Exception e) {
                    throw new CompilerException("close source token iterator failed: ", e);
                }
            }
            return false;
        }

        @Override
        public SourceToken next() throws CompileException {
            return stack.peek().next();
        }

        @Override
        public int getOffset() {
            return stack.peek().getOffset();
        }

        @Override
        public SourceToken current() throws CompileException {
            return stack.peek().current();
        }

        @Override
        public void close() throws Exception {
            while (!stack.isEmpty()) {
                stack.pop().close();
            }
        }

    }
}
