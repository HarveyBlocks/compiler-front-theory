package org.harvey.vie.theory.lexical.dfa;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusImpl;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;

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
        Set<AlphabetCharacter> motions = new HashSet<>();
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
                    accept = TokenType.morePriority(accept,tryAccept);
                    // errorOnAmbiguity(accept, tryAccept);
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

    @Deprecated
    private static void errorOnAmbiguity(TokenType accept, TokenType tryAccept) {
        throw new IllegalStateException(
                "It is not possible to confirm the type of token by the automaton, for ambiguity: " +
                accept.hint() +
                " and " +
                tryAccept.hint());
    }


    @AllArgsConstructor
    private static class StatusCombination {
        private final Set<NfaStatus> nfaStatuses;
        private final Set<AlphabetCharacter> motions;
        private final DfaStatus status;

        private void closureMove(NfaDfaContext ctx, Stack<StatusCombination> stack) {
            for (AlphabetCharacter motion : motions) {
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
