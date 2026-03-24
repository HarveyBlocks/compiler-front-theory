package org.harvey.vie.theory.lexical.dfa;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusImpl;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;
import org.harvey.vie.theory.source.character.SourceCharacter;

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
    @Override
    public DfaStatusGraph adapt(NfaStatusGraph nfaGraph) {
        NfaDfaContext ctx = new NfaDfaContext(nfaGraph);
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
    private static StatusCombination epsilonClosure(NfaDfaContext ctx, Set<NfaStatus> statusSet) {
        Set<SourceCharacter> motions = new HashSet<>();
        Set<NfaStatus> visited = new HashSet<>();
        TokenType accept = null;
        Stack<NfaStatus> stack = new Stack<>();
        for (NfaStatus nfaStatus : statusSet) {
            stack.push(nfaStatus);
        }
        while (!stack.isEmpty()) {
            NfaStatus top = stack.pop();
            if (visited.contains(top)) {
                continue;
            }
            visited.add(top);
            TokenType tryAccept = ctx.matchAccept(top);
            if (tryAccept != null) {
                if (accept == null) {
                    accept = tryAccept;
                } else if (tryAccept != accept) {
                    throw new IllegalStateException(
                            "It is not possible to confirm the type of token by the automaton, for ambiguity: " +
                            accept.hint() +
                            " and " +
                            accept.hint());
                }
            }
            motions.addAll(top.motions());
            List<NfaStatus> nextSteps = top.moveEpsilon();
            for (NfaStatus nextStep : nextSteps) {
                stack.push(nextStep);
            }
        }
        TokenType a = accept;
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
