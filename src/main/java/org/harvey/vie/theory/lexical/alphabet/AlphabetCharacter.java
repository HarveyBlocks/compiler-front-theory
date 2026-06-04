package org.harvey.vie.theory.lexical.alphabet;

import org.harvey.vie.theory.io.ILoader;

/**
 * Represents a character within the alphabet used by the lexical analyzer's
 * finite automata. It provides abstraction over different character representations
 * (like ASCII or Unicode) and allows for matching against input code points.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 17:35
 */
public interface AlphabetCharacter extends Comparable<AlphabetCharacter> {
    AlphabetCharacter UNSUPPORTED = new UnsupportedCharacter();
    int UNSUPPORTED_UNIQUE_CODE = -1;

    /**
     * 函数功能：判断字母表字符是否匹配指定码点。
     * 输入：
     * - codePoint：待匹配的字符码点。
     * 输出：是否匹配的布尔值。
     */
    boolean match(int codePoint);

    /**
     * 函数功能：获取字母表字符的唯一编码。
     * 输入：
     * - 无。
     * 输出：字符唯一编码整数。
     */
    int uniqueCode();

    interface Loader<T extends AlphabetCharacter> extends ILoader<T> {
    }
}

class UnsupportedCharacter extends AbstractAlphabetCharacter {
    /**
     * 函数功能：创建不支持字符对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    UnsupportedCharacter() {}

    /**
     * 函数功能：判断不支持字符是否匹配指定码点。
     * 输入：
     * - codePoint：待匹配的字符码点。
     * 输出：是否匹配的布尔值。
     */
    @Override
    public boolean match(int codePoint) {
        return false;
    }

    /**
     * 函数功能：获取不支持字符的唯一编码。
     * 输入：
     * - 无。
     * 输出：不支持字符的唯一编码整数。
     */
    @Override
    public int uniqueCode() {
        return AlphabetCharacter.UNSUPPORTED_UNIQUE_CODE;
    }

    /**
     * 函数功能：获取不支持字符的字符串表示。
     * 输入：
     * - 无。
     * 输出：不支持字符的字符串表示。
     */
    @Override
    public String toString() {
        return "UNSUPPORTED";
    }
}
