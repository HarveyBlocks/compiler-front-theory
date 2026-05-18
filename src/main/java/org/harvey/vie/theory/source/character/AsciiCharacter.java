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

    @Override
    public byte[] toCharacter() {
        return new byte[]{ascii};
    }

    @Override
    public String toString() {
        return Character.toString(ascii);
    }
}
