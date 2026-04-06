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
    AlphabetCharacter createRaw(int ch);

    AlphabetCharacter createEscape(int ch);

    AlphabetCharacter byUniqueCode(int uniqueCode);
}
