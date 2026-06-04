package org.harvey.vie.theory.syntax.td;

import lombok.Getter;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.lexical.TokenFilterPredict;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.PanicSourceTokenIterator;
import org.harvey.vie.theory.syntax.SyntaxParsingContext;
import org.harvey.vie.theory.syntax.grammar.symbol.GrammarUnitSymbol;
import org.harvey.vie.theory.syntax.grammar.symbol.TerminalSymbol;
import org.harvey.vie.theory.syntax.td.table.PredictiveParsingTable;

import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-03 13:13
 */
@Getter
public class PredicativeSyntaxParsingContext implements SyntaxParsingContext<GrammarUnitSymbol> {
    public static final TerminalSymbol END_MARK = PredictiveParsingTable.END_MARK_SYMBOL;
    private final Stack<GrammarUnitSymbol> symbolStack;
    private final PanicSourceTokenIterator iterator;
    private final ErrorContext errorContext;
    private final GrammarUnitSymbol start;
    /**
     * 函数功能：创建 PredicativeSyntaxParsingContext 对象。
     * 输入：
     * - start：GrammarUnitSymbol 类型参数。
     * - iterator：SourceTokenIterator 类型参数。
     * - errorContext：ErrorContext 类型参数。
     * - tokenFilterPredict：TokenFilterPredict 类型参数。
     * 输出：无。
     */
    public PredicativeSyntaxParsingContext(
            GrammarUnitSymbol start,
            SourceTokenIterator iterator,
            ErrorContext errorContext,
            TokenFilterPredict tokenFilterPredict) {
        this.iterator = new PanicSourceTokenIterator(iterator, errorContext, tokenFilterPredict);
        this.errorContext = errorContext;
        // --symbol--
        this.symbolStack = new Stack<>();
        symbolStack.push(END_MARK);
        symbolStack.push(start);
        this.start = start;
    }
/**
 * 函数功能：获取当前词法单元。
 * 输入：
 * - 无。
 * 输出：SourceToken 类型返回值。
 */


    @Override
    public SourceToken currentToken() {
        return iterator.currentToken();

    }
/**
 * 函数功能：判断是否存在下一个元素。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
/**
 * 函数功能：判断语法分析栈是否为空。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean isStackEmpty() {
        return symbolStack.isEmpty();
    }
/**
 * 函数功能：查看当前元素但不消费。
 * 输入：
 * - 无。
 * 输出：GrammarUnitSymbol 类型返回值。
 */

    @Override
    public GrammarUnitSymbol peek() {
        return symbolStack.peek();
    }
/**
 * 函数功能：判断当前栈是否满足接受条件。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean validAcceptStack() {
        return symbolStack.size() == 1 && symbolStack.peek() == END_MARK;
    }
/**
 * 函数功能：弹出当前栈顶元素。
 * 输入：
 * - 无。
 * 输出：无。
 */

    @Override
    public void pop() {
        symbolStack.pop();
    }
/**
 * 函数功能：消费当前词法单元。
 * 输入：
 * - 无。
 * 输出：无。
 */

    @Override
    public void consumeCurrentToken() {
        iterator.consumeCurrentToken();
    }
/**
 * 函数功能：压入一个元素。
 * 输入：
 * - next：GrammarUnitSymbol 类型参数。
 * 输出：无。
 */


    @Override
    public void push(GrammarUnitSymbol next) {
        symbolStack.push(next);
    }


}
