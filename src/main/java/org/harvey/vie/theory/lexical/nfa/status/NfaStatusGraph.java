package org.harvey.vie.theory.lexical.nfa.status;

import org.harvey.vie.theory.lexical.analysis.token.TokenType;

import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 17:42
 */
public interface NfaStatusGraph {

    NfaStatus getStart();


    TokenType matchAccept(NfaStatus status);
}
