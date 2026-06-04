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
/**
 * 函数功能：创建 SplitTokenResolver 对象。
 * 输入：
 * - splitter：ConflictTokenSplitter 类型参数。
 * 输出：无。
 */

    public SplitTokenResolver(ConflictTokenSplitter splitter) {this.splitter = splitter;}


    /**
     * 函数功能：解析词法冲突并返回词法单元迭代器。
     * 输入：
     * - terminal：TerminalSymbol 类型参数。
     * - old：SourceTokenIterator 类型参数。
     * 输出：SourceTokenIterator 类型返回值。
     */
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
/**
 * 函数功能：创建 SolvedIterator 对象。
 * 输入：
 * - origin：SourceTokenIterator 类型参数。
 * 输出：无。
 */

        SolvedIterator(SourceTokenIterator origin) {
            this.stack = new Stack<>();
            stack.push(origin);
        }
/**
 * 函数功能：判断是否存在下一个元素。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

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
/**
 * 函数功能：获取下一个元素。
 * 输入：
 * - 无。
 * 输出：SourceToken 类型返回值。
 */

        @Override
        public SourceToken next() throws CompileException {
            return stack.peek().next();
        }
/**
 * 函数功能：获取当前偏移量。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

        @Override
        public int getOffset() {
            return stack.peek().getOffset();
        }
/**
 * 函数功能：获取当前元素。
 * 输入：
 * - 无。
 * 输出：SourceToken 类型返回值。
 */

        @Override
        public SourceToken current() throws CompileException {
            return stack.peek().current();
        }
/**
 * 函数功能：关闭当前资源。
 * 输入：
 * - 无。
 * 输出：无。
 */

        @Override
        public void close() throws Exception {
            while (!stack.isEmpty()) {
                stack.pop().close();
            }
        }

    }
}
