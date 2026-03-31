package org.harvey.vie.theory.lexical.nfa.status;

import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;

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
public interface NfaStatus {

    NfaStatus move(AlphabetCharacter c);

    List<NfaStatus> moveEpsilon();

    Set<AlphabetCharacter> motions();

    void addEpsilonNext(NfaStatus next);

    NfaStatus computeNextIfAbsent(AlphabetCharacter c, Supplier<NfaStatus> supplier);

    int getId();

    boolean equals(Object obj);

    int hashCode();
}
