package org.harvey.vie.theory.source.character;

/**
 * Interface representing a character unit from the source code input.
 * This can represent a literal character or special markers like end-of-file (EOF).
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 10:36
 */
public interface SourceCharacter {
    SourceCharacter EOF = new EofCharacter();

    /**
     * 函数功能：将源字符转换为字节数组表示。
     * 输入：
     * - 无。
     * 输出：源字符对应的字节数组。
     */
    byte[] toCharacter();
}

class EofCharacter implements SourceCharacter {
    /**
     * 函数功能：将文件结束字符转换为字节数组表示。
     * 输入：
     * - 无。
     * 输出：文件结束字符对应的字节数组。
     */
    @Override
    public byte[] toCharacter() {
        throw new UnsupportedOperationException();
    }

    /**
     * 函数功能：返回文件结束字符的字符串表示。
     * 输入：
     * - 无。
     * 输出：文件结束字符的字符串表示。
     */
    @Override
    public String toString() {
        return "EOF";
    }
}
