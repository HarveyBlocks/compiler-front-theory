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


    /**
     * 函数功能：将单个正则与类型组合转换为默认 NFA 状态图。
     * 输入：
     * - pair：正则表达式节点与词法类型的组合。
     * - idGenerator：用于生成状态编号的生成器。
     * 输出：转换得到的默认 NFA 状态图。
     */
    private static DefaultNfaStatusGraph<AlphabetCharacter, TokenType> adapt(
            RegexTypePair pair,
            IdGenerator idGenerator) {
        NfaStatusPair statusPair = adapt(pair.getNode(), idGenerator);
        return new DefaultNfaStatusGraph<>(statusPair.start, Map.of(statusPair.end, pair.getType()));
    }

    /**
     * 函数功能：创建一个新的 NFA 状态。
     * 输入：
     * - idGenerator：用于生成状态编号的生成器。
     * 输出：新创建的 NFA 状态。
     */
    private static NfaStatus<AlphabetCharacter> instanceStatus(IdGenerator idGenerator) {
        return new NfaStatusImpl<>(idGenerator.next());
    }

    /**
     * 函数功能：将正则表达式节点转换为 NFA 起止状态对。
     * 输入：
     * - node：待转换的正则表达式节点。
     * - idGenerator：用于生成状态编号的生成器。
     * 输出：转换得到的 NFA 起止状态对。
     */
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

    /**
     * 函数功能：将空串正则节点转换为 NFA 起止状态对。
     * 输入：
     * - ignore：空串正则节点。
     * - idGenerator：用于生成状态编号的生成器。
     * 输出：转换得到的 NFA 起止状态对。
     */
    private static NfaStatusPair adapt(EpsilonRegexNode ignore, IdGenerator idGenerator) {
        NfaStatus<AlphabetCharacter> start = instanceStatus(idGenerator);
        NfaStatus<AlphabetCharacter> end = instanceStatus(idGenerator);
        start.addEpsilonNext(end);
        return new NfaStatusPair(start, end);
    }

    /**
     * 函数功能：将字符正则节点转换为 NFA 起止状态对。
     * 输入：
     * - node：字符正则节点。
     * - idGenerator：用于生成状态编号的生成器。
     * 输出：转换得到的 NFA 起止状态对。
     */
    private static NfaStatusPair adapt(CharRegexNode node, IdGenerator idGenerator) {
        NfaStatus<AlphabetCharacter> start = instanceStatus(idGenerator);
        NfaStatus<AlphabetCharacter> end = start.computeNextIfAbsent(
                node.getCharacter(),
                () -> instanceStatus(idGenerator)
        );
        return new NfaStatusPair(start, end);
    }

    /**
     * 函数功能：将连接正则节点转换为 NFA 起止状态对。
     * 输入：
     * - node：连接正则节点。
     * - idGenerator：用于生成状态编号的生成器。
     * 输出：转换得到的 NFA 起止状态对。
     */
    private static NfaStatusPair adapt(ConcatenationRegexNode node, IdGenerator idGenerator) {
        NfaStatusPair left = adapt(node.getLeft(), idGenerator);
        NfaStatusPair right = adapt(node.getRight(), idGenerator);
        left.end.addEpsilonNext(right.start);
        return new NfaStatusPair(left.start, right.end);
    }

    /**
     * 函数功能：将选择正则节点转换为 NFA 起止状态对。
     * 输入：
     * - node：选择正则节点。
     * - idGenerator：用于生成状态编号的生成器。
     * 输出：转换得到的 NFA 起止状态对。
     */
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

    /**
     * 函数功能：将闭包正则节点转换为 NFA 起止状态对。
     * 输入：
     * - node：闭包正则节点。
     * - idGenerator：用于生成状态编号的生成器。
     * 输出：转换得到的 NFA 起止状态对。
     */
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

    /**
     * 函数功能：将多个正则与类型组合转换为统一的 NFA 状态图。
     * 输入：
     * - pairs：正则表达式节点与词法类型的组合列表。
     * 输出：转换得到的 NFA 状态图。
     */
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

    /**
     * 函数功能：将单个正则与类型组合转换为默认 NFA 状态图。
     * 输入：
     * - pair：正则表达式节点与词法类型的组合。
     * 输出：转换得到的默认 NFA 状态图。
     */
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
