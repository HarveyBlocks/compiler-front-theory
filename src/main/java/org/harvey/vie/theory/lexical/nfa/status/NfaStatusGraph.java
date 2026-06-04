package org.harvey.vie.theory.lexical.nfa.status;

/**
 * Interface representing the complete graph structure of a Non-deterministic
 * Finite Automaton (NFA).
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 17:42
 */
public interface NfaStatusGraph<M, V> {

    /**
     * 函数功能：获取 NFA 状态图的起始状态。
     * 输入：
     * - 无。
     * 输出：起始 NFA 状态。
     */
    NfaStatus<M> getStart();


    /**
     * 函数功能：获取指定 NFA 状态对应的接受值。
     * 输入：
     * - status：待匹配的 NFA 状态。
     * 输出：状态对应的接受值；不存在则返回 null。
     */
    V matchAccept(NfaStatus<M> status);
}
