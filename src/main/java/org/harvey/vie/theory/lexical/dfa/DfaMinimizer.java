package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;

/**
 * Interface for components that minimize the number of states in a
 * Deterministic Finite Automaton (DFA) while preserving its recognized language.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 22:10
 */
public interface DfaMinimizer {
    DfaStatusTable minimize(DfaStatusGraph dfaStatus);
}
