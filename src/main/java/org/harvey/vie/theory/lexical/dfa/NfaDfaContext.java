package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 22:01
 */
public class NfaDfaContext {
    private final NfaStatusGraph nfaGraph;
    private final Map<Set<NfaStatus>, DfaStatus> visitedClosure;

    public NfaDfaContext(NfaStatusGraph nfaGraph) {
        this.nfaGraph = nfaGraph;
        visitedClosure = new HashMap<>();
    }

    public TokenType matchAccept(NfaStatus status) {
        return nfaGraph.matchAccept(status);
    }

    public Set<NfaStatus> startSet() {
        return Set.of(nfaGraph.getStart());
    }

    public Collection<DfaStatus> statusList() {
        return visitedClosure.values();
    }

    public DfaStatus computeVisitedClosureIfAbsent(Set<NfaStatus> visited, Supplier<DfaStatus> supplier) {
        return visitedClosure.computeIfAbsent(visited, (k) -> supplier.get());
    }
}
