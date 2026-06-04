package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatus;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusGraph;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Context class used during the subset construction process (NFA to DFA conversion).
 * it maintains the mapping between sets of NFA states and their corresponding
 * individual DFA states to ensure each unique set is only processed once.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 22:01
 */
public class NfaDfaContext<M, V extends StatusVertex> {
    private final NfaStatusGraph<M, V> nfaGraph;
    private final Map<Set<NfaStatus<M>>, DfaStatus<M, V>> visitedClosure;

    /**
     * 函数功能：创建 NFA 转 DFA 上下文。
     * 输入：
     * - nfaGraph：待转换的 NFA 状态图。
     * 输出：无。
     */
    public NfaDfaContext(NfaStatusGraph<M, V> nfaGraph) {
        this.nfaGraph = nfaGraph;
        visitedClosure = new HashMap<>();
    }

    /**
     * 函数功能：查询 NFA 状态对应的接受标记。
     * 输入：
     * - status：待查询的 NFA 状态。
     * 输出：状态对应的接受标记；非接受状态返回 null。
     */
    public V matchAccept(NfaStatus<M> status) {
        return nfaGraph.matchAccept(status);
    }

    /**
     * 函数功能：获取包含 NFA 起始状态的集合。
     * 输入：
     * - 无。
     * 输出：起始状态集合。
     */
    public Set<NfaStatus<M>> startSet() {
        return Set.of(nfaGraph.getStart());
    }

    /**
     * 函数功能：获取已创建的 DFA 状态集合。
     * 输入：
     * - 无。
     * 输出：DFA 状态集合。
     */
    public Collection<DfaStatus<M, V>> statusList() {
        return visitedClosure.values();
    }

    /**
     * 函数功能：获取或创建指定 NFA 状态闭包对应的 DFA 状态。
     * 输入：
     * - visited：NFA 状态闭包集合。
     * - supplier：闭包不存在时创建 DFA 状态的供应器。
     * 输出：闭包对应的 DfaStatus。
     */
    public DfaStatus<M, V> computeVisitedClosureIfAbsent(
            Set<NfaStatus<M>> visited,
            Supplier<DfaStatus<M, V>> supplier) {
        return visitedClosure.computeIfAbsent(visited, (k) -> supplier.get());
    }
}
