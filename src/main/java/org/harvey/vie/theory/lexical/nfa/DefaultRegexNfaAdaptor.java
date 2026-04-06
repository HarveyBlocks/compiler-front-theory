package org.harvey.vie.theory.lexical.nfa;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.RegexTypePair;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.nfa.status.DefaultNfaStatusGraph;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusImpl;
import org.harvey.vie.theory.lexical.regex.node.*;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the {@link RegexNfaAdaptor} interface.
 * It uses Thompson's construction algorithm to systematically transform a
 * regular expression parse tree into a Non-deterministic Finite Automaton (NFA).
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 14:17
 */
public class DefaultRegexNfaAdaptor implements RegexNfaAdaptor {


    private static DefaultNfaStatusGraph<AlphabetCharacter, TokenType> adapt(
            RegexTypePair pair,
            IdGenerator idGenerator) {
        NfaStatusPair statusPair = adapt(pair.getNode(), idGenerator);
        return new DefaultNfaStatusGraph<>(statusPair.start, Map.of(statusPair.end, pair.getType()));
    }

    private static NfaStatus<AlphabetCharacter> instanceStatus(IdGenerator idGenerator) {
        return new NfaStatusImpl<>(idGenerator.next());
    }

    private static NfaStatusPair adapt(RegexNode node, IdGenerator idGenerator) {
        if (node instanceof CharRegexNode) {
            return adapt((CharRegexNode) node, idGenerator);
        } else if (node instanceof ClosureRegexNode) {
            return adapt((ClosureRegexNode) node, idGenerator);
        } else if (node instanceof ConcatenationRegexNode) {
            return adapt((ConcatenationRegexNode) node, idGenerator);
        } else if (node instanceof CupRegexNode) {
            return adapt((CupRegexNode) node, idGenerator);
        } else if (node instanceof EpsilonRegexNode) {
            return adapt((EpsilonRegexNode) node, idGenerator);
        } else {
            throw new IllegalStateException("Unknown class of: " + node.getClass());
        }
    }

    private static NfaStatusPair adapt(EpsilonRegexNode ignore, IdGenerator idGenerator) {
        NfaStatus<AlphabetCharacter> start = instanceStatus(idGenerator);
        NfaStatus<AlphabetCharacter> end = instanceStatus(idGenerator);
        start.addEpsilonNext(end);
        return new NfaStatusPair(start, end);
    }

    private static NfaStatusPair adapt(CharRegexNode node, IdGenerator idGenerator) {
        NfaStatus<AlphabetCharacter> start = instanceStatus(idGenerator);
        NfaStatus<AlphabetCharacter> end = start.computeNextIfAbsent(
                node.getCharacter(),
                () -> instanceStatus(idGenerator)
        );
        return new NfaStatusPair(start, end);
    }

    private static NfaStatusPair adapt(ConcatenationRegexNode node, IdGenerator idGenerator) {
        NfaStatusPair left = adapt(node.getLeft(), idGenerator);
        NfaStatusPair right = adapt(node.getRight(), idGenerator);
        left.end.addEpsilonNext(right.start);
        return new NfaStatusPair(left.start, right.end);
    }

    private static NfaStatusPair adapt(CupRegexNode node, IdGenerator idGenerator) {
        NfaStatus<AlphabetCharacter> start = instanceStatus(idGenerator);
        NfaStatusPair left = adapt(node.getLeft(), idGenerator);
        NfaStatusPair right = adapt(node.getRight(), idGenerator);
        start.addEpsilonNext(left.start);
        start.addEpsilonNext(right.start);
        NfaStatus<AlphabetCharacter> end = instanceStatus(idGenerator);
        left.end.addEpsilonNext(end);
        right.end.addEpsilonNext(end);
        return new NfaStatusPair(start, end);
    }

    private static NfaStatusPair adapt(ClosureRegexNode node, IdGenerator idGenerator) {
        NfaStatus<AlphabetCharacter> start = instanceStatus(idGenerator);
        NfaStatusPair child = adapt(node.getChild(), idGenerator);
        NfaStatus<AlphabetCharacter> end = instanceStatus(idGenerator);
        start.addEpsilonNext(child.start);
        start.addEpsilonNext(end);
        child.end.addEpsilonNext(child.start);
        child.end.addEpsilonNext(end);
        return new NfaStatusPair(start, end);
    }

    @Override
    public NfaStatusGraph<AlphabetCharacter, TokenType> adapt(List<RegexTypePair> pairs) {
        IdGenerator idGenerator = new IdGenerator();
        NfaStatusImpl<AlphabetCharacter> start = new NfaStatusImpl<>(idGenerator.next());
        Map<NfaStatus<AlphabetCharacter>, TokenType> ends = new HashMap<>();
        pairs.stream().map(p -> adapt(p, idGenerator)).forEach(g -> {
            start.addEpsilonNext(g.getStart());
            ends.putAll(g.getEnds());
        });
        return new DefaultNfaStatusGraph<>(start, ends);
    }

    @Override
    public DefaultNfaStatusGraph<AlphabetCharacter, TokenType> adapt(RegexTypePair pair) {
        return adapt(pair, new IdGenerator());
    }

    @AllArgsConstructor
    private static class NfaStatusPair {
        private final NfaStatus<AlphabetCharacter> start;
        private final NfaStatus<AlphabetCharacter> end;
    }


}
