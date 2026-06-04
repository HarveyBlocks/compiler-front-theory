package org.harvey.vie.theory.lexical.nfa.status;

/**
 * Abstract base class for {@link NfaStatus} implementations.
 * Provides log identity logic using the state's unique ID.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 20:15
 */
public abstract class AbstractNfaStatus<M> implements NfaStatus<M> {
    /**
     * 函数功能：返回当前 NFA 状态的哈希值。
     * 输入：
     * - 无。
     * 输出：状态哈希值。
     */
    @Override
    public int hashCode() {
        return getId();
    }

    /**
     * 函数功能：判断当前 NFA 状态是否与指定对象相等。
     * 输入：
     * - obj：待比较的对象。
     * 输出：若对象表示相同状态则返回 true，否则返回 false。
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NfaStatus && ((NfaStatus<?>) obj).getId() == getId();
    }

}
