package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

import java.util.Set;

/**
 * Interface representing a state in a Deterministic Finite Automaton (DFA).
 * Each state can have transitions to other states based on input characters
 * and may be an accepting state for a specific {@link TokenType}.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:21
 */
public interface DfaStatus<M, V extends StatusVertex> {
    /**
     * 函数功能：按指定动作获取 DFA 后继状态。
     * 输入：
     * - motion：状态转移动作。
     * 输出：后继 DfaStatus；不存在转移时返回 null。
     */
    DfaStatus<M, V> move(M motion);

    /**
     * 函数功能：获取当前状态支持的全部转移动作。
     * 输入：
     * - 无。
     * 输出：转移动作集合。
     */
    Set<M> motions();

    /**
     * 函数功能：设置指定动作对应的后继状态。
     * 输入：
     * - motion：状态转移动作。
     * - next：目标后继状态。
     * 输出：是否新增转移的布尔值。
     */
    boolean setNext(M motion, DfaStatus<M, V> next);

    /**
     * 函数功能：获取当前状态的接受标记。
     * 输入：
     * - 无。
     * 输出：接受标记；非接受状态返回 null。
     */
    V accept();
}
