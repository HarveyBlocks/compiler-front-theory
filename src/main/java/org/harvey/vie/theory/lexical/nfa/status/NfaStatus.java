package org.harvey.vie.theory.lexical.nfa.status;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Interface representing a state in a Non-deterministic Finite Automaton (NFA).
 * It defines methods for state transitions based on input characters or
 * epsilon (empty) transitions, which are fundamental to NFA behavior.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 13:47
 */
public interface NfaStatus<M> {

    /**
     * 函数功能：根据输入符号获取当前状态的下一个 NFA 状态。
     * 输入：
     * - c：用于状态转移的输入符号。
     * 输出：转移后的 NFA 状态；不存在则返回 null。
     */
    NfaStatus<M> move(M c);

    /**
     * 函数功能：获取当前状态通过空转移可达的 NFA 状态列表。
     * 输入：
     * - 无。
     * 输出：空转移可达的 NFA 状态列表。
     */
    List<NfaStatus<M>> moveEpsilon();

    /**
     * 函数功能：获取当前状态支持的输入符号集合。
     * 输入：
     * - 无。
     * 输出：可用于状态转移的输入符号集合。
     */
    Set<M> motions();

    /**
     * 函数功能：为当前状态添加一个空转移目标状态。
     * 输入：
     * - next：空转移指向的 NFA 状态。
     * 输出：无。
     */
    void addEpsilonNext(NfaStatus<M> next);

    /**
     * 函数功能：获取指定输入符号对应的下一状态，若不存在则创建。
     * 输入：
     * - c：用于状态转移的输入符号。
     * - supplier：用于创建下一状态的供应器。
     * 输出：输入符号对应的 NFA 状态。
     */
    NfaStatus<M> computeNextIfAbsent(M c, Supplier<NfaStatus<M>> supplier);

    /**
     * 函数功能：获取当前 NFA 状态的编号。
     * 输入：
     * - 无。
     * 输出：当前状态的编号。
     */
    int getId();

    /**
     * 函数功能：判断当前 NFA 状态是否与指定对象相等。
     * 输入：
     * - obj：待比较的对象。
     * 输出：若对象表示相同状态则返回 true，否则返回 false。
     */
    boolean equals(Object obj);

    /**
     * 函数功能：返回当前 NFA 状态的哈希值。
     * 输入：
     * - 无。
     * 输出：状态哈希值。
     */
    int hashCode();
}
