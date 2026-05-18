package org.harvey.vie.theory.lexical.dfa.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

import java.util.Collection;

/**
 * Represents the complete graph of a Deterministic Finite Automaton (DFA),
 * containing the start state and the entire pool of states.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 22:21
 */
@AllArgsConstructor
@Getter
public class DfaStatusGraph<M, V extends StatusVertex> {
    private final DfaStatus<M, V> start;
    private final Collection<DfaStatus<M, V>> pool;
}
