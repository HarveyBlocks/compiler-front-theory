package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

/**
 * Interface for components that convert a Non-deterministic Finite Automaton (NFA)
 * into a Deterministic Finite Automaton (DFA).
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:24
 */
public interface NfaDfaAdaptor {
    /**
     * 函数功能：将 NFA 状态图转换为 DFA 状态图。
     * 输入：
     * - nfaGraph：待转换的 NFA 状态图。
     * 输出：转换得到的 DfaStatusGraph。
     */
    <M, V extends StatusVertex> DfaStatusGraph<M, V> adapt(NfaStatusGraph<M, V> nfaGraph);
}
