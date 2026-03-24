package org.harvey.vie.theory.lexical.dfa;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusImpl;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusTable;
import org.harvey.vie.theory.source.SourceCharacter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:27
 */
public class DefaultNfaDfaAdaptor implements NfaDfaAdaptor {


    public DefaultNfaDfaAdaptor() {
    }

    @Override
    public DfaStatusGraph adapt(NfaStatusTable table) {
        NfaDfaContext ctx = new NfaDfaContext(table);
        // 1. 对起始做
        StatusCombination startCombination = epsilonClosure(ctx, ctx.startSet());
        // 2. loop
        Stack<StatusCombination> stack = new Stack<>();
        stack.push(startCombination);
        while (!stack.isEmpty()) {
            StatusCombination top = stack.pop();
            top.closureMove(ctx, stack);
        }
        return new DfaStatusGraph(startCombination.status, ctx.statusList());
    }

    @NonNull
    private static StatusCombination epsilonClosure(NfaDfaContext ctx, Set<NfaStatus> nfaStartSet) {
        Set<SourceCharacter> motions = new HashSet<>();
        Set<NfaStatus> visited = new HashSet<>();
        boolean accept = false;
        Stack<NfaStatus> stack = new Stack<>();
        for (NfaStatus nfaStatus : nfaStartSet) {
            stack.push(nfaStatus);
        }
        while (!stack.isEmpty()) {
            NfaStatus top = stack.pop();
            if (visited.contains(top)) {
                continue;
            }
            visited.add(top);
            if (!accept && ctx.endEquals(top)) {
                accept = true;
            }
            motions.addAll(top.motions());
            List<NfaStatus> nextSteps = top.moveEpsilon();
            for (NfaStatus nextStep : nextSteps) {
                stack.push(nextStep);
            }
        }
        boolean a = accept;
        DfaStatus dfaStatus = ctx.computeVisitedClosureIfAbsent(visited, () -> new DfaStatusImpl(a));
        return new StatusCombination(visited, motions, dfaStatus);
    }


    @AllArgsConstructor
    private static class StatusCombination {
        private final Set<NfaStatus> nfaStatuses;
        private final Set<SourceCharacter> motions;
        private final DfaStatus status;

        private void closureMove(NfaDfaContext ctx, Stack<StatusCombination> stack) {
            for (SourceCharacter motion : motions) {
                Set<NfaStatus> nfaStatuseSet = nfaStatuses.stream()
                        .map(s -> s.move(motion))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                StatusCombination statusCombination = epsilonClosure(ctx, nfaStatuseSet);
                boolean newMotion = status.setNext(motion, statusCombination.status);
                if (newMotion) {
                    stack.push(statusCombination);
                }
            }
        }
    }
}
