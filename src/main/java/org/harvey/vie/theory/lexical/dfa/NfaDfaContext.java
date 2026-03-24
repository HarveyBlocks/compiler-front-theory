package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusTable;

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
    private final NfaStatus nfaStart;
    private final NfaStatus nfaEnd;
    private final Map<Set<NfaStatus>, DfaStatus> visitedClosure;

    public NfaDfaContext(NfaStatusTable table) {
        this.nfaStart = table.getStart();
        this.nfaEnd = table.getEnd();
        visitedClosure = new HashMap<>();
    }

    public boolean endEquals(NfaStatus status) {
        return nfaEnd.equals(status);
    }

    public Set<NfaStatus> startSet() {
        return Set.of(nfaStart);
    }

    public Collection<DfaStatus> statusList() {
        return visitedClosure.values();
    }

    public DfaStatus computeVisitedClosureIfAbsent(Set<NfaStatus> visited, Supplier<DfaStatus> supplier) {
        return visitedClosure.computeIfAbsent(visited, (k) -> supplier.get());
    }
}
