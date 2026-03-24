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
public class OtherCharacter implements SourceCharacter {
    private final char ch;
    public OtherCharacter(char ch) {
        this.ch = ch;
    }

    @Override
    public int hashCode() {
        return ch;
    }

    @Override
    public String toString() {
        return ch + "";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof OtherCharacter) && ((OtherCharacter) obj).ch == ch;
    }

    @Override
    public int compareTo(SourceCharacter o) {
        return hashCode() - o.hashCode();
    }
}
