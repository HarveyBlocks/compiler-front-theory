package org.harvey.vie.theory.lexical.alphabet;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.lexical.regex.RegexOperator;

/**
 * Factory class for creating {@link AlphabetCharacter} instances specifically
 * for use in regular expression parsing. It handles both raw characters and
 * escaped characters, ensuring appropriate character types (e.g., ASCII or Unicode)
 * are instantiated based on the input.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 11:03
 */
@Slf4j
public class RegexAlphabetCharacterFactory implements AlphabetCharacterFactory {
    private static final AlphabetCharacter[] POOL = new AlphabetCharacter[128];

    @Override
    public AlphabetCharacter createRaw(int ch) {
        if (ch < 128) {
            return getAscii((byte) ch);
        }
        log.warn("It is not recommended to use non-ASCII characters in the compiler's rules");
        return new CodePointAlphabetCharacter(ch);
    }

    private static AlphabetCharacter getAscii(byte ch) {
        return POOL[ch] != null ? POOL[ch] : (POOL[ch] = new AsciiAlphabetCharacter(ch));
    }

    @Override
    public AlphabetCharacter createEscape(int ch) {
        if (ch < 128) {
            RegexOperator regexOperator = RegexOperator.regexOperator((byte) ch);
            if (regexOperator != null) {
                return getAscii(regexOperator.getC());
            }
        }
        log.warn("Unsupported escape characters, which will be recognized as normal characters.");
        return createRaw(ch);
    }

    @Override
    public AlphabetCharacter byUniqueCode(int uniqueCode) {
        if (uniqueCode==AlphabetCharacter.UNSUPPORTED.uniqueCode()){
            return AlphabetCharacter.UNSUPPORTED;
        }
        return createRaw(uniqueCode);
    }
}
