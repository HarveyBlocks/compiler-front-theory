package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTableFactory;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

/**
 * Interface for components that minimize the number of states in a
 * Deterministic Finite Automaton (DFA) while preserving its recognized language.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 22:10
 */
public interface DfaMinimizer {
    /**
     * 函数功能：最小化 DFA 状态表。
     * 输入：
     * - factory：用于创建状态表的工厂。
     * - dfaStatus：待最小化的 DFA 状态图。
     * 输出：最小化后的 DFA 状态表。
     */
    <M, V extends StatusVertex, P extends DfaStatusTable<M, V>> P minimize(
            DfaStatusTableFactory<M, V, P> factory,
            DfaStatusGraph<M, V> dfaStatus);
}
