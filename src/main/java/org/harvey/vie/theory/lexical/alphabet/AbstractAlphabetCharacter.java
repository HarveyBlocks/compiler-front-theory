package org.harvey.vie.theory.lexical.alphabet;

/**
 * Abstract base class for {@link AlphabetCharacter} implementations.
 * It provides default behavior for equality, hashing, and comparison based
 * on the unique character code.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 17:54
 */
public abstract class AbstractAlphabetCharacter implements AlphabetCharacter {
    @Override
    public int hashCode() {
        return uniqueCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof AlphabetCharacter && ((AlphabetCharacter) obj).uniqueCode() == uniqueCode();
    }

    @Override
    public int compareTo(AlphabetCharacter o) {
        return uniqueCode() - o.uniqueCode();
    }
}
