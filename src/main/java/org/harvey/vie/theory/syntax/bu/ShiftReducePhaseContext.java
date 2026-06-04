package org.harvey.vie.theory.syntax.bu;

import lombok.Data;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.SyntaxErrorMessage;
import org.harvey.vie.theory.lexical.TokenFilterPredict;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.lexical.analysis.token.SourceTokenIterator;
import org.harvey.vie.theory.syntax.PanicSourceTokenIterator;
import org.harvey.vie.theory.syntax.SyntaxParsingContext;
import org.harvey.vie.theory.syntax.bu.table.ShiftReduceParsingTable;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.symbol.HeadSymbol;

import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-07 15:07
 */
@Data
public class ShiftReducePhaseContext implements SyntaxParsingContext<Integer> {
    private final ShiftReduceParsingTable table;
    private final Stack<Integer> statusStack;
    private final int startStatus;
    private PanicSourceTokenIterator iterator;
    private final ErrorContext errorContext;
/**
 * 函数功能：创建 ShiftReducePhaseContext 对象。
 * 输入：
 * - table：ShiftReduceParsingTable 类型参数。
 * - iterator：SourceTokenIterator 类型参数。
 * - tokenFilterPredict：TokenFilterPredict 类型参数。
 * - errorContext：ErrorContext 类型参数。
 * 输出：无。
 */

    public ShiftReducePhaseContext(
            ShiftReduceParsingTable table,
            SourceTokenIterator iterator,
            TokenFilterPredict tokenFilterPredict,
            ErrorContext errorContext) {
        this.table = table;
        this.iterator = new PanicSourceTokenIterator(iterator, errorContext, tokenFilterPredict);
        this.errorContext = errorContext;
        this.statusStack = new Stack<>();
        this.startStatus = table.getStart();
        push(startStatus);
    }
/**
 * 函数功能：判断语法分析栈是否为空。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean isStackEmpty() {
        return statusStack.isEmpty();
    }
/**
 * 函数功能：查看当前元素但不消费。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    @Override
    public Integer peek() {
        return statusStack.peek();
    }
/**
 * 函数功能：判断当前栈是否满足接受条件。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean validAcceptStack() {
        return statusStack.size() == 1 && statusStack.peek() == startStatus;
    }
/**
 * 函数功能：压入一个元素。
 * 输入：
 * - next：Integer 类型参数。
 * 输出：无。
 */


    @Override
    public void push(Integer next) {
        statusStack.push(next);
    }
/**
 * 函数功能：获取起始元素。
 * 输入：
 * - 无。
 * 输出：整数结果。
 */

    @Override
    public Integer getStart() {
        return startStatus;
    }
/**
 * 函数功能：弹出当前栈顶元素。
 * 输入：
 * - 无。
 * 输出：无。
 */

    @Override
    public void pop() {
        statusStack.pop();
    }
/**
 * 函数功能：判断是否存在下一个元素。
 * 输入：
 * - 无。
 * 输出：判断结果布尔值。
 */

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
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
 * 函数功能：获取当前词法单元。
 * 输入：
 * - 无。
 * 输出：SourceToken 类型返回值。
 */

    @Override
    public SourceToken currentToken() {
        return this.iterator.currentToken();
    }
/**
 * 函数功能：获取规约后跳转的状态。
 * 输入：
 * - top：int 类型参数。
 * - head：HeadSymbol 类型参数。
 * 输出：整数结果。
 */

    public int gotoNext(int top, HeadSymbol head) {
        return table.gotoNext(top, head);
    }
    /**
     * 函数功能：添加语法错误信息。
     * 输入：
     * - offset：int 类型参数。
     * - message：String 类型参数。
     * 输出：无。
     */
    public void addError(int offset, String message) {
        errorContext.addError(new SyntaxErrorMessage(offset,message));
    }
    /**
     * 函数功能：获取产生式编号。
     * 输入：
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：整数结果。
     */
    public int getProductionId(SimpleGrammarProduction production) {
        Integer id = table.getProductionId(production);
        Objects.requireNonNull(id, ()-> "can not found id in table by the production: " + production);
        return id;
    }
/**
 * 函数功能：获取状态栈的字符串表示。
 * 输入：
 * - 无。
 * 输出：字符串结果。
 */

    public String statusStackString() {
        return statusStack.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" ", "[", "]"));
    }
}
