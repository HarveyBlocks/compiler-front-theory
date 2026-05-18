package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 19:35
 */
public class RegexDfaStatusTableFactory implements
        DfaStatusTableFactory<AlphabetCharacter, TokenType, RegexDfaStatusTable> {
    @Override
    public RegexDfaStatusTable produce(
            int[][] newStates,
            AlphabetCharacter[] alphabet,
            int newStart,
            TokenType[] accepts) {
        return new RegexDfaStatusTable(newStates, alphabet, newStart, accepts);
    }

    @Override
    public TokenType[] newVertexArray(int length) {
        return new TokenType[length];
    }

    @Override
    public AlphabetCharacter[] newMotionArray(int length) {
        return new AlphabetCharacter[length];
    }
}
