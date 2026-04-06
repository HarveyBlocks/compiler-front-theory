package org.harvey.vie.theory.lexical.dfa;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusImpl;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@link NfaDfaAdaptor} interface.
 * This class is responsible for converting a Non-deterministic Finite Automaton (NFA)
 * into a Deterministic Finite Automaton (DFA) using the subset construction algorithm,
 * also known as the epsilon-closure algorithm.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:27
 */
public class DefaultNfaDfaAdaptor implements NfaDfaAdaptor {
    @NonNull
    private static <M, V extends StatusVertex> StatusCombination<M, V> epsilonClosure(
            NfaDfaContext<M, V> ctx,
            Set<NfaStatus<M>> statusSet) {
        Set<M> motions = new HashSet<>();
        Set<NfaStatus<M>> visited = new HashSet<>();
        V accept = null;
        Stack<NfaStatus<M>> stack = new Stack<>();
        for (NfaStatus<M> nfaStatus : statusSet) {
            stack.push(nfaStatus);
        }
        while (!stack.isEmpty()) {
            NfaStatus<M> top = stack.pop();
            if (visited.contains(top)) {
                continue;
            }
            visited.add(top);
            V tryAccept = ctx.matchAccept(top);
            if (tryAccept != null) {
                if (accept == null) {
                    accept = tryAccept;
                } else if (tryAccept != accept) {
                    accept = StatusVertex.morePriority(accept, tryAccept);
                    // errorOnAmbiguity(accept, tryAccept);
                }
            }
            motions.addAll(top.motions());
            List<NfaStatus<M>> nextSteps = top.moveEpsilon();
            for (NfaStatus<M> nextStep : nextSteps) {
                stack.push(nextStep);
            }
        }
        V a = accept;
        DfaStatus<M, V> dfaStatus = ctx.computeVisitedClosureIfAbsent(visited, () -> new DfaStatusImpl<>(a));
        return new StatusCombination<>(visited, motions, dfaStatus);
    }

    @Deprecated
    private static void errorOnAmbiguity(TokenType accept, TokenType tryAccept) {
        throw new IllegalStateException(
                "It is not possible to confirm the type of token by the automaton, for ambiguity: " +
                accept.hint() +
                " and " +
                tryAccept.hint());
    }

    @Override
    public <M, V extends StatusVertex> DfaStatusGraph<M, V> adapt(NfaStatusGraph<M, V> nfaGraph) {
        NfaDfaContext<M, V> ctx = new NfaDfaContext<>(nfaGraph);
        // 1. 对起始做
        StatusCombination<M, V> startCombination = epsilonClosure(ctx, ctx.startSet());
        // 2. loop
        Stack<StatusCombination<M, V>> stack = new Stack<>();
        stack.push(startCombination);
        while (!stack.isEmpty()) {
            StatusCombination<M, V> top = stack.pop();
            top.closureMove(ctx, stack);
        }
        return new DfaStatusGraph<>(startCombination.status, ctx.statusList());
    }

    @AllArgsConstructor
    private static class StatusCombination<M, V extends StatusVertex> {
        private final Set<NfaStatus<M>> nfaStatuses;
        private final Set<M> motions;
        private final DfaStatus<M, V> status;

        private void closureMove(NfaDfaContext<M, V> ctx, Stack<StatusCombination<M, V>> stack) {
            for (M motion : motions) {
                Set<NfaStatus<M>> nfaStatuseSet = nfaStatuses.stream()
                        .map(s -> s.move(motion))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                StatusCombination<M, V> statusCombination = epsilonClosure(ctx, nfaStatuseSet);
                boolean newMotion = status.setNext(motion, statusCombination.status);
                if (newMotion) {
                    stack.push(statusCombination);
                }
            }
        }
    }
}
