package org.harvey.vie.theory.lexical.nfa.status;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Concrete implementation of {@link NfaStatus}.
 * It manages transitions to other NFA states via input characters and
 * epsilon transitions, maintaining a unique state identifier.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 13:47
 */
@Getter
@Setter
public class NfaStatusImpl<M> extends AbstractNfaStatus<M> {
    private final int id;
    private final Map<M, NfaStatus<M>> nextSteps;
    private final List<NfaStatus<M>> epsilonNextSteps;

    public NfaStatusImpl(int id) {
        this.id = id;
        this.nextSteps = new HashMap<>();
        this.epsilonNextSteps = new ArrayList<>();
    }

    // res 转 nfa, 以构建合适
    @Override
    public NfaStatus<M> move(M c) {
        return this.nextSteps.get(c);
    }

    @Override
    public List<NfaStatus<M>> moveEpsilon() {
        return epsilonNextSteps;
    }

    @Override
    public Set<M> motions() {
        return nextSteps.keySet();
    }

    @Override
    public void addEpsilonNext(NfaStatus<M> next) {
        this.epsilonNextSteps.add(next);
    }

    @Override
    public NfaStatus<M> computeNextIfAbsent(M c, Supplier<NfaStatus<M>> supplier) {
        return this.nextSteps.computeIfAbsent(c, (k) -> supplier.get());
    }

    @Override
    public String toString() {
        return id +
               "-ε>" +
               epsilonNextSteps.stream()
                       .map(NfaStatus::getId)
                       .map(Objects::toString)
                       .collect(Collectors.joining(",", "[", "]")) +
               "|->" +
               nextSteps.entrySet()
                       .stream()
                       .map(s -> s.getKey() + ":" + s.getValue().getId())
                       .collect(Collectors.joining(",", "{", "}"));
    }
}
