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
    private final byte ascii;

    @Override
    public boolean match(int codePoint) {
        return codePoint < 128 && codePoint >= 0 && ascii == (byte) codePoint;
    }

    @Override
    public int uniqueCode() {
        return ascii;
    }

    private static final Map<Byte, String> ESCAPE_MAP = Map.of(
            (byte) '\f', "\\f",
            (byte) '\t', "\\t",
            (byte) '\r', "\\r",
            (byte) '\n', "\\n",
            (byte) ' ', "` `"
    );

    @Override
    public String toString() {
        return Optional.ofNullable(ESCAPE_MAP.get(ascii)).orElseGet(() -> Character.toString(ascii));
    }

}
