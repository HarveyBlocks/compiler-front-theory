package org.harvey.vie.theory.lexical.alphabet;

/**
 * Interface for factories that createTerminal {@link AlphabetCharacter} instances.
 * This allows for different strategies in character creation, such as handling
 * raw input, escaped sequences, or unique internal codes.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 17:36
 */
public interface AlphabetCharacterFactory {
    /**
     * 函数功能：根据原始字符码点创建字母表字符。
     * 输入：
     * - ch：原始字符码点。
     * 输出：创建得到的 AlphabetCharacter。
     */
    AlphabetCharacter createRaw(int ch);

    /**
     * 函数功能：根据转义字符码点创建字母表字符。
     * 输入：
     * - ch：转义字符码点。
     * 输出：创建得到的 AlphabetCharacter。
     */
    AlphabetCharacter createEscape(int ch);

    /**
     * 函数功能：根据唯一编码还原字母表字符。
     * 输入：
     * - uniqueCode：字母表字符唯一编码。
     * 输出：还原得到的 AlphabetCharacter。
     */
    AlphabetCharacter byUniqueCode(int uniqueCode);
}
