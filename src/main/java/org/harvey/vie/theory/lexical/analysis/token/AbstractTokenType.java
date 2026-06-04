package org.harvey.vie.theory.lexical.analysis.token;

/**
 * Abstract base class for {@link TokenType} implementations.
 * Provides default equality and hashing logic based on the token type's hint.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 17:25
 */
public abstract class AbstractTokenType implements TokenType {
    /**
     * 函数功能：计算词法单元类型的哈希值。
     * 输入：
     * - 无。
     * 输出：词法单元类型哈希值整数。
     */
    @Override
    public int hashCode() {
        return hint().hashCode();
    }

    /**
     * 函数功能：判断对象是否与当前词法单元类型相等。
     * 输入：
     * - obj：待比较的对象。
     * 输出：是否相等的布尔值。
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof TokenType && hint().equals(((TokenType) obj).hint());
    }

    /**
     * 函数功能：获取词法单元类型的字符串表示。
     * 输入：
     * - 无。
     * 输出：词法单元类型提示字符串。
     */
    @Override
    public String toString() {
        return hint();
    }
}
