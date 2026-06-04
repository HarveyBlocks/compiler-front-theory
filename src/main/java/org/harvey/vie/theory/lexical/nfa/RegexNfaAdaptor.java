package org.harvey.vie.theory.lexical.nfa;

import org.harvey.vie.theory.lexical.RegexTypePair;
import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;

import java.util.List;

/**
 * Interface for components that adapt regular expression structures into
 * Non-deterministic Finite Automata (NFA).
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:21
 */
public interface RegexNfaAdaptor {
    /**
     * 函数功能：将多个正则与类型组合转换为 NFA 状态图。
     * 输入：
     * - pairs：正则表达式节点与词法类型的组合列表。
     * 输出：转换得到的 NFA 状态图。
     */
    NfaStatusGraph<AlphabetCharacter, TokenType> adapt(List<RegexTypePair> pairs);

    /**
     * 函数功能：将单个正则与类型组合转换为 NFA 状态图。
     * 输入：
     * - pair：正则表达式节点与词法类型的组合。
     * 输出：转换得到的 NFA 状态图。
     */
    NfaStatusGraph<AlphabetCharacter, TokenType> adapt(RegexTypePair pair);
}
