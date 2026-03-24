package org.harvey.vie.theory.source;

import lombok.Getter;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 10:37
 */
@Getter
public class AsciiCharacter implements SourceCharacter {
    private final char ascii;

    public AsciiCharacter(char ch) {
        this.ascii = ch;
    }

    @Override
    public int hashCode() {
        return ascii;
    }

    @Override
    public String toString() {
        return ascii + "";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof AsciiCharacter) && ((AsciiCharacter) obj).ascii == ascii;
    }

    @Override
    public int compareTo(SourceCharacter o) {
        return hashCode() - o.hashCode();
    }
}
