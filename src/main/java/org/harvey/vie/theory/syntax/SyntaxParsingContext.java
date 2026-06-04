package org.harvey.vie.theory.syntax;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-12 21:57
 */
public interface SyntaxParsingContext<T> {
    T getStart();

    /**
     * 函数功能：弹出当前栈顶元素。
     * 输入：
     * - 无。
     * 输出：无。
     */

    void pop();

    /**
     * 函数功能：压入一个元素。
     * 输入：
     * - next：T 类型参数。
     * 输出：无。
     */

    void push(T next);

    /**
     * 函数功能：判断是否存在下一个元素。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */
    boolean hasNext();

    /**
     * 函数功能：消费当前词法单元。
     * 输入：
     * - 无。
     * 输出：无。
     */
    void consumeCurrentToken();

    /**
     * 函数功能：获取当前词法单元。
     * 输入：
     * - 无。
     * 输出：SourceToken 类型返回值。
     */

    SourceToken currentToken();

    /**
     * 函数功能：判断语法分析栈是否为空。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean isStackEmpty();

    T peek();

    /**
     * 函数功能：判断当前栈是否满足接受条件。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    boolean validAcceptStack();
}
