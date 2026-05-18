package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

import java.util.Set;

/**
 * Interface representing a state in a Deterministic Finite Automaton (DFA).
 * Each state can have transitions to other states based on input characters
 * and may be an accepting state for a specific {@link TokenType}.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:21
 */
public interface DfaStatus<M, V extends StatusVertex> {
    /**
     * @return null if no motion in this status' follow-up
     */
    DfaStatus<M, V> move(M motion);

    Set<M> motions();

    /**
     * @return true if new motion
     * @throws IllegalStateException throw if motion is exist and value is different
     */
    boolean setNext(M motion, DfaStatus<M, V> next);

    /**
     * @return null for not accept
     */
    V accept();
}
