package org.harvey.vie.theory.lexical.alphabet;

import org.harvey.vie.theory.io.ILoader;

/**
 * Represents a character within the alphabet used by the lexical analyzer's
 * finite automata. It provides abstraction over different character representations
 * (like ASCII or Unicode) and allows for matching against input code points.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 17:35
 */
public interface AlphabetCharacter extends Comparable<AlphabetCharacter> {
    AlphabetCharacter UNSUPPORTED = new UnsupportedCharacter();
    int UNSUPPORTED_UNIQUE_CODE = -1;

    boolean match(int codePoint);

    int uniqueCode();

    interface Loader<T extends AlphabetCharacter> extends ILoader<T> {
    }
}

class UnsupportedCharacter extends AbstractAlphabetCharacter {
    UnsupportedCharacter() {}

    @Override
    public boolean match(int codePoint) {
        return false;
    }

    @Override
    public int uniqueCode() {
        return AlphabetCharacter.UNSUPPORTED_UNIQUE_CODE;
    }

    @Override
    public String toString() {
        return "UNSUPPORTED";
    }
}
