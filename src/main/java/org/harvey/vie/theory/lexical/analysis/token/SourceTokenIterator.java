package org.harvey.vie.theory.lexical.analysis.token;

import org.harvey.vie.theory.exception.CompileException;

/**
 * Interface for an iterator that produces {@link SourceToken} instances
 * from a segment of source text.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 15:14
 */
public interface SourceTokenIterator extends AutoCloseable {
    SourceToken NO_MORE_TOKEN = new DoneToken();

    /**
     * 函数功能：判断是否还有可读取的源词法单元。
     * 输入：
     * - 无。
     * 输出：是否存在下一个词法单元的布尔值。
     */
    boolean hasNext();

    /**
     * 函数功能：读取下一个源词法单元。
     * 输入：
     * - 无。
     * 输出：下一个 SourceToken。
     */
    SourceToken next() throws CompileException;

    /**
     * 函数功能：获取当前读取偏移量。
     * 输入：
     * - 无。
     * 输出：当前偏移量整数。
     */
    int getOffset();

    /**
     * 函数功能：获取当前源词法单元。
     * 输入：
     * - 无。
     * 输出：当前 SourceToken。
     */
    SourceToken current() throws CompileException;
}

class DoneToken implements SourceToken {
    /**
     * 函数功能：获取结束词法单元的提示字符串。
     * 输入：
     * - 无。
     * 输出：结束词法单元提示字符串。
     */
    @Override
    public String hintString() {
        return "DONE";
    }

    /**
     * 函数功能：获取结束词法单元的词素字节数组。
     * 输入：
     * - 无。
     * 输出：空 byte 数组。
     */
    @Override
    public byte[] getLexeme() {
        return new byte[0];
    }

    /**
     * 函数功能：获取结束词法单元的偏移量。
     * 输入：
     * - 无。
     * 输出：结束词法单元偏移量整数。
     */
    @Override
    public int getOffset() {
        return -1;
    }

    /**
     * 函数功能：获取结束词法单元的字符串表示。
     * 输入：
     * - 无。
     * 输出：结束词法单元字符串。
     */
    @Override
    public String toString() {
        return hintString();
    }

    /**
     * 函数功能：获取结束词法单元的类型。
     * 输入：
     * - 无。
     * 输出：结束词法单元类型；固定为 null。
     */
    @Override
    public TokenType getType() {
        return null;
    }

}
