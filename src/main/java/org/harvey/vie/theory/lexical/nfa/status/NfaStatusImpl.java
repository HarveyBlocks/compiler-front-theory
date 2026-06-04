package org.harvey.vie.theory.lexical.nfa.status;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Concrete implementation of {@link NfaStatus}.
 * It manages transitions to other NFA states via input characters and
 * epsilon transitions, maintaining a unique state identifier.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 13:47
 */
@Getter
@Setter
public class NfaStatusImpl<M> extends AbstractNfaStatus<M> {
    private final int id;
    private final Map<M, NfaStatus<M>> nextSteps;
    private final List<NfaStatus<M>> epsilonNextSteps;

    /**
     * 函数功能：创建指定编号的 NFA 状态。
     * 输入：
     * - id：NFA 状态编号。
     * 输出：无。
     */
    public NfaStatusImpl(int id) {
        this.id = id;
        this.nextSteps = new HashMap<>();
        this.epsilonNextSteps = new ArrayList<>();
    }

    // res 转 nfa, 以构建合适
    /**
     * 函数功能：根据输入符号获取当前状态的下一个 NFA 状态。
     * 输入：
     * - c：用于状态转移的输入符号。
     * 输出：转移后的 NFA 状态；不存在则返回 null。
     */
    @Override
    public NfaStatus<M> move(M c) {
        return this.nextSteps.get(c);
    }

    /**
     * 函数功能：获取当前状态通过空转移可达的 NFA 状态列表。
     * 输入：
     * - 无。
     * 输出：空转移可达的 NFA 状态列表。
     */
    @Override
    public List<NfaStatus<M>> moveEpsilon() {
        return epsilonNextSteps;
    }

    /**
     * 函数功能：获取当前状态支持的输入符号集合。
     * 输入：
     * - 无。
     * 输出：可用于状态转移的输入符号集合。
     */
    @Override
    public Set<M> motions() {
        return nextSteps.keySet();
    }

    /**
     * 函数功能：为当前状态添加一个空转移目标状态。
     * 输入：
     * - next：空转移指向的 NFA 状态。
     * 输出：无。
     */
    @Override
    public void addEpsilonNext(NfaStatus<M> next) {
        this.epsilonNextSteps.add(next);
    }

    /**
     * 函数功能：获取指定输入符号对应的下一状态，若不存在则创建。
     * 输入：
     * - c：用于状态转移的输入符号。
     * - supplier：用于创建下一状态的供应器。
     * 输出：输入符号对应的 NFA 状态。
     */
    @Override
    public NfaStatus<M> computeNextIfAbsent(M c, Supplier<NfaStatus<M>> supplier) {
        return this.nextSteps.computeIfAbsent(c, (k) -> supplier.get());
    }

    /**
     * 函数功能：返回当前 NFA 状态的字符串表示。
     * 输入：
     * - 无。
     * 输出：状态及其转移关系的字符串表示。
     */
    @Override
    public String toString() {
        return id +
               "-ε>" +
               epsilonNextSteps.stream()
                       .map(NfaStatus::getId)
                       .map(Objects::toString)
                       .collect(Collectors.joining(",", "[", "]")) +
               "|->" +
               nextSteps.entrySet()
                       .stream()
                       .map(s -> s.getKey() + ":" + s.getValue().getId())
                       .collect(Collectors.joining(",", "{", "}"));
    }
}
