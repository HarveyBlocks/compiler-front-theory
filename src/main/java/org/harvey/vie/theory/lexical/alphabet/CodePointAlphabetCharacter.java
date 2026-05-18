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

    @Override
    public boolean match(int codePoint) {
        return this.codePoint == codePoint;
    }

    @Override
    public int uniqueCode() {
        return this.codePoint;
    }


    @Override
    public String toString() {
        return Character.toString(codePoint);
    }

}
