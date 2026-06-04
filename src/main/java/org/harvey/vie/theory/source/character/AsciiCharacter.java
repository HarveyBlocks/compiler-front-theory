package org.harvey.vie.theory.source.character;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Implementation of {@link SourceCharacter} that encapsulates a single ASCII character.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 10:37
 */
@Getter
@AllArgsConstructor
public class AsciiCharacter implements SourceCharacter {
    private final byte ascii;

    /**
     * 函数功能：将 ASCII 源字符转换为字节数组表示。
     * 输入：
     * - 无。
     * 输出：ASCII 源字符对应的字节数组。
     */
    @Override
    public byte[] toCharacter() {
        return new byte[]{ascii};
    }

    /**
     * 函数功能：返回 ASCII 源字符的字符串表示。
     * 输入：
     * - 无。
     * 输出：ASCII 源字符的字符串表示。
     */
    @Override
    public String toString() {
        return Character.toString(ascii);
    }
}
