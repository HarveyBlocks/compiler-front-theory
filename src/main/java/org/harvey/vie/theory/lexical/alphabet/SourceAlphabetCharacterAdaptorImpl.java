package org.harvey.vie.theory.lexical.alphabet;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.source.character.AsciiCharacter;
import org.harvey.vie.theory.source.character.CodePointCharacter;
import org.harvey.vie.theory.source.character.SourceCharacter;

/**
 * Implementation of {@link SourceAlphabetCharacterAdaptor} that uses an
 * {@link AlphabetCharacterFactory} to transform {@link SourceCharacter}
 * instances into {@link AlphabetCharacter} instances suitable for the automaton.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 18:20
 */
@AllArgsConstructor
public class SourceAlphabetCharacterAdaptorImpl implements SourceAlphabetCharacterAdaptor {
    private AlphabetCharacterFactory factory;

    @Override
    public AlphabetCharacter adapt(SourceCharacter ch) {
        if (ch instanceof AsciiCharacter) {
            return factory.createRaw(((AsciiCharacter) ch).getAscii());
        } else if (ch instanceof CodePointCharacter) {
            return factory.createRaw(((CodePointCharacter) ch).getCodePoint());
        }
        return AlphabetCharacter.UNSUPPORTED;
    }
}
