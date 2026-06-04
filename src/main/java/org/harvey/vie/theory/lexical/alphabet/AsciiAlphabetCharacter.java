package org.harvey.vie.theory.lexical.alphabet;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link AlphabetCharacter} representing a single-byte ASCII character.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 17:37
 */
@Getter
@AllArgsConstructor
public class AsciiAlphabetCharacter extends AbstractAlphabetCharacter {
    private static final Map<Byte, String> ESCAPE_MAP = Map.of(
            (byte) '\f', "\\f",
            (byte) '\t', "\\t",
            (byte) '\r', "\\r",
            (byte) '\n', "\\n",
            (byte) ' ', "` `"
    );
    private final byte ascii;

    /**
     * 函数功能：判断 ASCII 字符是否匹配指定码点。
     * 输入：
     * - codePoint：待匹配的字符码点。
     * 输出：是否匹配的布尔值。
     */
    @Override
    public boolean match(int codePoint) {
        return codePoint < 128 && codePoint >= 0 && ascii == (byte) codePoint;
    }

    /**
     * 函数功能：获取 ASCII 字符的唯一编码。
     * 输入：
     * - 无。
     * 输出：ASCII 字符唯一编码整数。
     */
    @Override
    public int uniqueCode() {
        return ascii;
    }

    /**
     * 函数功能：获取 ASCII 字符的字符串表示。
     * 输入：
     * - 无。
     * 输出：ASCII 字符字符串。
     */
    @Override
    public String toString() {
        return Optional.ofNullable(ESCAPE_MAP.get(ascii)).orElseGet(() -> Character.toString(ascii));
    }

}
