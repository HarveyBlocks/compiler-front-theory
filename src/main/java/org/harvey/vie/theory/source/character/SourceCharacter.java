package org.harvey.vie.theory.source.character;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 10:36
 */
public interface SourceCharacter extends Comparable<SourceCharacter> {
    SourceCharacter EOF = new EofCharacter();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    char toCharacter();
}

class EofCharacter implements SourceCharacter {
    @Override
    public int hashCode() {
        return -1;
    }

    @Override
    public char toCharacter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EofCharacter;
    }

    @Override
    public int compareTo(SourceCharacter o) {
        throw new UnsupportedOperationException();
    }
}
