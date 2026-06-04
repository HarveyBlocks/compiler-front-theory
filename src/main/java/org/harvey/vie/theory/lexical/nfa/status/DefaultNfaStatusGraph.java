package org.harvey.vie.theory.lexical.nfa.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Default implementation of the {@link NfaStatusGraph} interface.
 * It encapsulates the structure of an NFA, including its entry point (start state)
 * and the exit points (accepting states) mapped to their token types.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 17:41
 */
@Getter
@AllArgsConstructor
public class DefaultNfaStatusGraph<M, V> implements NfaStatusGraph<M, V> {
    private final NfaStatus<M> start;
    private final Map<NfaStatus<M>, V> ends;

    /**
     * 函数功能：获取指定 NFA 状态对应的接受值。
     * 输入：
     * - status：待匹配的 NFA 状态。
     * 输出：状态对应的接受值；不存在则返回 null。
     */
    @Override
    public V matchAccept(NfaStatus<M> status) {
        return ends.get(status);
    }
}
