package org.harvey.vie.theory.source;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 10:56
 */
public interface SourceCharacterFactory {
    SourceCharacter createRaw(char ch);
    SourceCharacter createEscape(char ch);
}
