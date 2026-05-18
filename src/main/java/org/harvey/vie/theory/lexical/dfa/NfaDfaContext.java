package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Context class used during the subset construction process (NFA to DFA conversion).
 * it maintains the mapping between sets of NFA states and their corresponding
 * individual DFA states to ensure each unique set is only processed once.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 22:01
 */
public class NfaDfaContext<M, V extends StatusVertex> {
    private final NfaStatusGraph<M, V> nfaGraph;
    private final Map<Set<NfaStatus<M>>, DfaStatus<M, V>> visitedClosure;

    public NfaDfaContext(NfaStatusGraph<M, V> nfaGraph) {
        this.nfaGraph = nfaGraph;
        visitedClosure = new HashMap<>();
    }

    public V matchAccept(NfaStatus<M> status) {
        return nfaGraph.matchAccept(status);
    }

    public Set<NfaStatus<M>> startSet() {
        return Set.of(nfaGraph.getStart());
    }

    public Collection<DfaStatus<M, V>> statusList() {
        return visitedClosure.values();
    }

    public DfaStatus<M, V> computeVisitedClosureIfAbsent(
            Set<NfaStatus<M>> visited,
            Supplier<DfaStatus<M, V>> supplier) {
        return visitedClosure.computeIfAbsent(visited, (k) -> supplier.get());
    }
}
