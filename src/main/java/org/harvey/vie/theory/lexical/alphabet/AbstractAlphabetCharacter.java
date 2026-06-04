package org.harvey.vie.theory.lexical.alphabet;

/**
 * Abstract base class for {@link AlphabetCharacter} implementations.
 * It provides default behavior for equality, hashing, and comparison based
 * on the unique character code.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 17:54
 */
public abstract class AbstractAlphabetCharacter implements AlphabetCharacter {
    /**
     * 函数功能：计算字母表字符的哈希值。
     * 输入：
     * - 无。
     * 输出：字母表字符的哈希值整数。
     */
    @Override
    public int hashCode() {
        return uniqueCode();
    }

    /**
     * 函数功能：判断对象是否与当前字母表字符相等。
     * 输入：
     * - obj：待比较的对象。
     * 输出：是否相等的布尔值。
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this ||
               obj instanceof AlphabetCharacter && ((AlphabetCharacter) obj).uniqueCode() == uniqueCode();
    }

    /**
     * 函数功能：比较当前字母表字符与另一字母表字符的顺序。
     * 输入：
     * - o：待比较的字母表字符。
     * 输出：字符唯一编码差值整数。
     */
    @Override
    public int compareTo(AlphabetCharacter o) {
        return uniqueCode() - o.uniqueCode();
    }
}
