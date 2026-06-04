package org.harvey.vie.theory.lexical.alphabet;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an {@link AlphabetCharacter} defined by a specific Unicode code point.
 * This class is used to handle characters that fall outside the standard ASCII range.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 17:39
 */
@Getter
@AllArgsConstructor
public class CodePointAlphabetCharacter extends AbstractAlphabetCharacter {
    private final int codePoint;

    /**
     * 函数功能：判断码点字符是否匹配指定码点。
     * 输入：
     * - codePoint：待匹配的字符码点。
     * 输出：是否匹配的布尔值。
     */
    @Override
    public boolean match(int codePoint) {
        return this.codePoint == codePoint;
    }

    /**
     * 函数功能：获取码点字符的唯一编码。
     * 输入：
     * - 无。
     * 输出：字符唯一编码整数。
     */
    @Override
    public int uniqueCode() {
        return this.codePoint;
    }


    /**
     * 函数功能：获取码点字符的字符串表示。
     * 输入：
     * - 无。
     * 输出：码点字符字符串。
     */
    @Override
    public String toString() {
        return Character.toString(codePoint);
    }

}
