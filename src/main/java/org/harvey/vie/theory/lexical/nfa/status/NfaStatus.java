package org.harvey.vie.theory.lexical.nfa.status;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Interface representing a state in a Non-deterministic Finite Automaton (NFA).
 * It defines methods for state transitions based on input characters or
 * epsilon (empty) transitions, which are fundamental to NFA behavior.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 13:47
 */
public interface NfaStatus<M> {

    NfaStatus<M> move(M c);

    List<NfaStatus<M>> moveEpsilon();

    Set<M> motions();

    void addEpsilonNext(NfaStatus<M> next);

    NfaStatus<M> computeNextIfAbsent(M c, Supplier<NfaStatus<M>> supplier);

    int getId();

    boolean equals(Object obj);

    int hashCode();
}
