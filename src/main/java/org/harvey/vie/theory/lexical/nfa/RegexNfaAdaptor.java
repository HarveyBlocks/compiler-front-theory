package org.harvey.vie.theory.lexical.nfa;

import org.harvey.vie.theory.lexical.RegexTypePair;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;

import java.util.List;

/**
 * Interface for components that adapt regular expression structures into
 * Non-deterministic Finite Automata (NFA).
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:21
 */
public interface RegexNfaAdaptor {
    NfaStatusGraph<AlphabetCharacter, TokenType> adapt(List<RegexTypePair> pairs);
    NfaStatusGraph<AlphabetCharacter, TokenType> adapt(RegexTypePair pair);
}
