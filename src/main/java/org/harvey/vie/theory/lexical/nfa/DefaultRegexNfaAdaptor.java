package org.harvey.vie.theory.lexical.nfa;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.nfa.status.DefaultNfaStatusTable;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusImpl;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusTable;
import org.harvey.vie.theory.lexical.regex.node.*;
import org.harvey.vie.theory.util.IdGenerator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 14:17
 */
public class DefaultRegexNfaAdaptor implements RegexNfaAdaptor {


    @Override
    public NfaStatusTable adapt(RegexNode node) {
        NfaStatusPair pair = adapt0(node, new IdGenerator());
        return new DefaultNfaStatusTable(pair.start, pair.end);
    }

    private static NfaStatus instanceStatus(IdGenerator idGenerator) {
        return new NfaStatusImpl(idGenerator.next());
    }

    private static NfaStatusPair adapt0(RegexNode node, IdGenerator idGenerator) {
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
        NfaStatus start = instanceStatus(idGenerator);
        NfaStatus end = instanceStatus(idGenerator);
        start.addEpsilonNext(end);
        return new NfaStatusPair(start, end);
    }

    private static NfaStatusPair adapt(CharRegexNode node, IdGenerator idGenerator) {
        NfaStatus start = instanceStatus(idGenerator);
        NfaStatus end = start.computeNextIfAbsent(node.getCharacter(), () -> instanceStatus(idGenerator));
        return new NfaStatusPair(start, end);
    }

    private static NfaStatusPair adapt(ConcatenationRegexNode node, IdGenerator idGenerator) {
        NfaStatusPair left = adapt0(node.getLeft(), idGenerator);
        NfaStatusPair right = adapt0(node.getRight(), idGenerator);
        left.end.addEpsilonNext(right.start);
        return new NfaStatusPair(left.start, right.end);
    }

    private static NfaStatusPair adapt(CupRegexNode node, IdGenerator idGenerator) {
        NfaStatus start = instanceStatus(idGenerator);
        NfaStatusPair left = adapt0(node.getLeft(), idGenerator);
        NfaStatusPair right = adapt0(node.getRight(), idGenerator);
        start.addEpsilonNext(left.start);
        start.addEpsilonNext(right.start);
        NfaStatus end = instanceStatus(idGenerator);
        left.end.addEpsilonNext(end);
        right.end.addEpsilonNext(end);
        return new NfaStatusPair(start, end);
    }

    private  static NfaStatusPair adapt(ClosureRegexNode node, IdGenerator idGenerator) {
        NfaStatus start = instanceStatus(idGenerator);
        NfaStatusPair child = adapt0(node.getChild(), idGenerator);
        NfaStatus end = instanceStatus(idGenerator);
        start.addEpsilonNext(child.start);
        start.addEpsilonNext(end);
        child.end.addEpsilonNext(child.start);
        child.end.addEpsilonNext(end);
        return new NfaStatusPair(start, end);
    }

    @AllArgsConstructor
    private static class NfaStatusPair {
        private final NfaStatus start;
        private final NfaStatus end;

    }


}
