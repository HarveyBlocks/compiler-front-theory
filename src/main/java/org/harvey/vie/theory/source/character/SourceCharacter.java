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
    byte[] toCharacter();
}

class EofCharacter implements SourceCharacter {
    @Override
    public byte[] toCharacter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "EOF";
    }
}
