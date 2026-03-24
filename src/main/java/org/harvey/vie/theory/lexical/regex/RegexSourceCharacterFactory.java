package org.harvey.vie.theory.lexical.regex;

import lombok.extern.slf4j.Slf4j;
import org.harvey.vie.theory.source.AsciiCharacter;
import org.harvey.vie.theory.source.OtherCharacter;
import org.harvey.vie.theory.source.SourceCharacter;
import org.harvey.vie.theory.source.SourceCharacterFactory;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 11:03
 */
@Slf4j
public class RegexSourceCharacterFactory implements SourceCharacterFactory {
    private static final AsciiCharacter[] POOL = new AsciiCharacter[128];

    @Override
    public SourceCharacter createRaw(char ch) {
        if (ch < 128) {
            return getAscii(ch);
        }
        log.warn("It is not recommended to use non-ASCII characters in the compiler's rules");
        return new OtherCharacter(ch);
    }

    private static AsciiCharacter getAscii(char ch) {
        return POOL[ch] != null ? POOL[ch] : (POOL[ch] = new AsciiCharacter(ch));
    }

    @Override
    public SourceCharacter createEscape(char ch) {
        if (RegexOperator.isRegexOperator(ch)) {
            return getAscii(ch);
        }
        log.warn("Unsupported escape characters, which will be recognized as normal characters.");
        return createRaw(ch);
    }
}
