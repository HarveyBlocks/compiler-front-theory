package org.harvey.vie.theory.lexical.regex.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.source.character.SourceCharacter;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:24
 */

@Getter
@Setter
@AllArgsConstructor
public class CharRegexNode implements RegexNode {
    private final SourceCharacter character;

    @Override
    public String toString() {
        return "" + character;
    }
}
