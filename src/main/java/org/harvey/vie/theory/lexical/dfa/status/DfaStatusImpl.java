package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Concrete implementation of {@link DfaStatus}.
 * This class uses a map to manage deterministic transitions to next states
 * and stores the token type it accepts, if any.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:28
 */
public class DfaStatusImpl<M, V extends StatusVertex> implements DfaStatus<M, V> {
    private final V accept;
    private final Map<M, DfaStatus<M, V>> next;

    /**
     * 函数功能：创建 DFA 状态实现对象。
     * 输入：
     * - accept：当前状态的接受标记。
     * 输出：无。
     */
    public DfaStatusImpl(V accept) {
        this.accept = accept;
        this.next = new HashMap<>();
    }


    /**
     * 函数功能：按指定动作获取 DFA 后继状态。
     * 输入：
     * - motion：状态转移动作。
     * 输出：后继 DfaStatus；不存在转移时返回 null。
     */
    @Override
    public DfaStatus<M, V> move(M motion) {
        return next.get(motion);
    }

    /**
     * 函数功能：获取当前状态支持的全部转移动作。
     * 输入：
     * - 无。
     * 输出：转移动作集合。
     */
    @Override
    public Set<M> motions() {
        return next.keySet();
    }

    /**
     * 函数功能：设置指定动作对应的后继状态。
     * 输入：
     * - motion：状态转移动作。
     * - next：目标后继状态。
     * 输出：是否新增转移的布尔值。
     */
    @Override
    public boolean setNext(M motion, DfaStatus<M, V> next) {
        DfaStatus<M, V> status = this.next.get(motion);
        if (status == null) {
            this.next.put(motion, next);
            return true;
        } else if (status == next) {
            return false;
        } else {
            // 确定有限自动机, 依旧存在不确定性
            throw new IllegalStateException("Deterministic finite automaton still has nondeterministic motion");
        }
    }

    /**
     * 函数功能：获取当前状态的接受标记。
     * 输入：
     * - 无。
     * 输出：接受标记；非接受状态返回 null。
     */
    @Override
    public V accept() {
        return accept;
    }

    /**
     * 函数功能：获取 DFA 状态的字符串表示。
     * 输入：
     * - 无。
     * 输出：DFA 状态字符串。
     */
    @Override
    public String toString() {
        return "DfaStatus[" + (accept != null ? accept.hint() : "non-accepting") + ", transitions=" + next.size() + "]";
    }
}
