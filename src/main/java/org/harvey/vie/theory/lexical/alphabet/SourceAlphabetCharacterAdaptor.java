package org.harvey.vie.theory.lexical.alphabet;

import org.harvey.vie.theory.source.character.SourceCharacter;

/**
 * Interface for an adaptor that converts {@link SourceCharacter} instances
 * from the input stream into {@link AlphabetCharacter} instances used
 * by the lexical analyzer's state machines.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-25 17:59
 */
public interface SourceAlphabetCharacterAdaptor {
    /**
     * 函数功能：将源字符适配为字母表字符。
     * 输入：
     * - ch：待适配的源字符。
     * 输出：适配得到的 AlphabetCharacter。
     */
    AlphabetCharacter adapt(SourceCharacter ch);
}
