package org.harvey.vie.theory.lexical.nfa.status;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.source.character.SourceCharacter;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 13:47
 */
@Getter
@Setter
public class NfaStatusImpl extends AbstractNfaStatus {
    private final int id;
    private final Map<SourceCharacter, NfaStatus> nextSteps;
    private final List<NfaStatus> epsilonNextSteps;

    public NfaStatusImpl(int id) {
        this.id = id;
        this.nextSteps = new HashMap<>();
        this.epsilonNextSteps = new ArrayList<>();
    }

    // res 转 nfa, 以构建合适
    @Override
    public NfaStatus move(SourceCharacter c) {
        return this.nextSteps.get(c);
    }

    @Override
    public List<NfaStatus> moveEpsilon() {
        return epsilonNextSteps;
    }

    @Override
    public Set<SourceCharacter> motions() {
        return nextSteps.keySet();
    }

    @Override
    public void addEpsilonNext(NfaStatus next) {
        this.epsilonNextSteps.add(next);
    }

    @Override
    public NfaStatus computeNextIfAbsent(SourceCharacter c, Supplier<NfaStatus> supplier) {
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
