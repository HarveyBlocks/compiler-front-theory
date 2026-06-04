package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.dfa.status.*;
import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;
import org.harvey.vie.theory.util.IntArraySignature;

import java.util.*;

/**
 * Default implementation of the {@link DfaMinimizer} interface.
 * This class provides functionality to minimize a Deterministic Finite Automaton (DFA)
 * by partitioning states into equivalence classes. It follows the standard algorithm
 * for DFA minimization, ensuring the resulting DFA has the minimum number of states
 * while recognizing the same language.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 22:10
 */
public class DefaultDfaMinimizer implements DfaMinimizer {

    /**
     * DEAD
     */
    private static final int UNKNOWN_MOVE_STATUS = RegexDfaStatusTable.UNKNOWN_MOVE_STATUS;

    /**
     * 函数功能：从 DFA 状态集合中收集并排序字母表。
     * 输入：
     * - factory：用于创建字母表数组的状态表工厂。
     * - allStates：待分析的 DFA 状态集合。
     * 输出：收集得到的字母表数组。
     */
    private static <M, V extends StatusVertex, P extends DfaStatusTable<M, V>> M[] collectAlphabet(
            DfaStatusTableFactory<M, V, P> factory, Collection<DfaStatus<M, V>> allStates) {
        return allStates.stream()
                .map(DfaStatus::motions)
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .toArray(factory::newMotionArray);
    }

    /**
     * 函数功能：按接受类型初始化 DFA 状态分区。
     * 输入：
     * - allStates：待划分的 DFA 状态集合。
     * 输出：初始化后的 Partition。
     */
    private static <M, V extends StatusVertex> Partition<M, V> initDepart(Collection<DfaStatus<M, V>> allStates) {
        Map<V, Integer> typeIndexMap = new HashMap<>();
        Partition<M, V> partition = new Partition<>();
        // 1. non accept
        Block<M, V> nonAccepting = new Block<>(null);
        // 2. some different accept
        for (DfaStatus<M, V> s : allStates) {
            V accept = s.accept();
            if (accept == null) {
                nonAccepting.add(s);
                continue;
            }
            Integer index = typeIndexMap.get(accept);
            if (index != null) {
                partition.putStatusIdx(s, index);
                partition.get(index).add(1, s);
                continue;
            }
            typeIndexMap.put(accept, partition.size());
            Block<M, V> block = new Block<>(accept);
            block.add(s);
            partition.add(block);
        }
        if (!nonAccepting.isEmpty()) {
            partition.add(nonAccepting);
        }
        return partition;
    }

    /**
     * 函数功能：根据字母表转移结果细化 DFA 状态分区。
     * 输入：
     * - partition：待细化的状态分区。
     * - alphabet：用于比较转移的字母表数组。
     * 输出：细化后的 Partition。
     */
    private static <M, V extends StatusVertex> Partition<M, V> refinePartition(
            Partition<M, V> partition, M[] alphabet) {
        Partition<M, V> newPartition = new Partition<>();
        for (Block<M, V> block : partition) {
            if (block.size() == 1) {
                newPartition.add(block);
                continue;
            }
            // 分割
            Collection<Block<M, V>> group = groupByMotions(block, alphabet, partition.stateToBlockIdx);
            //noinspection UseBulkOperation
            group.forEach(newPartition::add);
        }
        return newPartition;
    }

    /**
     * 函数功能：按状态转移签名将状态块分组。
     * 输入：
     * - block：待分组的状态块。
     * - alphabet：用于计算转移签名的字母表数组。
     * - stateToBlockIdx：状态到分区块索引的映射。
     * 输出：分组后的状态块集合。
     */
    private static <M, V extends StatusVertex> Collection<Block<M, V>> groupByMotions(
            Block<M, V> block, M[] alphabet, Map<DfaStatus<M, V>, Integer> stateToBlockIdx) {
        Map<IntArraySignature, Block<M, V>> groups = new HashMap<>();
        for (DfaStatus<M, V> s : block) {
            // 计算状态在字母表上的转移目标块索引的签名。
            // 签名由每个状态对应的目标块索引组合而成。
            IntArraySignature signature = computeSignature(s, alphabet, stateToBlockIdx);
            groups.computeIfAbsent(signature, k -> new Block<>(block.accept)).add(s);
        }
        return groups.values();
    }

    /**
     * 函数功能：计算 DFA 状态在字母表上的转移签名。
     * 输入：
     * - s：待计算签名的 DFA 状态。
     * - alphabet：用于计算转移签名的字母表数组。
     * - stateToBlockIdx：状态到分区块索引的映射。
     * 输出：状态转移签名 IntArraySignature。
     */
    private static <M, V extends StatusVertex> IntArraySignature computeSignature(
            DfaStatus<M, V> s, M[] alphabet, Map<DfaStatus<M, V>, Integer> stateToBlockIdx) {
        return new IntArraySignature(Arrays.stream(alphabet)
                .map(s::move)
                .mapToInt(target -> target == null ? UNKNOWN_MOVE_STATUS : stateToBlockIdx.get(target))
                .toArray());
    }

    /**
     * 函数功能：根据稳定分区构建最小化后的 DFA 状态表。
     * 输入：
     * - factory：用于创建状态表的工厂。
     * - partition：稳定后的状态分区。
     * - alphabet：DFA 字母表数组。
     * - start：原始 DFA 起始状态。
     * 输出：最小化后的 DFA 状态表。
     */
    private static <M, V extends StatusVertex, P extends DfaStatusTable<M, V>> P buildMinimizedTable(
            DfaStatusTableFactory<M, V, P> factory, Partition<M, V> partition, M[] alphabet, DfaStatus<M, V> start) {
        // 每个块对应一个新状态
        int newStatesLength = partition.size();
        int[][] newStates = new int[newStatesLength][alphabet.length];
        V[] accepts = factory.newVertexArray(newStatesLength);
        for (int i = 0; i < newStatesLength; i++) {
            Block<M, V> block = partition.get(i);
            accepts[i] = block.accept; // null 就是 null
            DfaStatus<M, V> representative = block.iterator().next();// 任选一
            for (int j = 0; j < alphabet.length; j++) {
                DfaStatus<M, V> target = representative.move(alphabet[j]);
                newStates[i][j] = target == null ? UNKNOWN_MOVE_STATUS : partition.getIndexByStatus(target);
            }
        }
        // 定位新的起始状态
        int newStart = partition.getIndexByStatus(start);
        return factory.produce(newStates, alphabet, newStart, accepts);
    }

    /**
     * 函数功能：最小化 DFA 状态表。
     * 输入：
     * - factory：用于创建状态表的工厂。
     * - dfaStatus：待最小化的 DFA 状态图。
     * 输出：最小化后的 DFA 状态表。
     */
    @Override
    public <M, V extends StatusVertex, P extends DfaStatusTable<M, V>> P minimize(
            DfaStatusTableFactory<M, V, P> factory, DfaStatusGraph<M, V> dfaStatus) {
        Collection<DfaStatus<M, V>> allStates = dfaStatus.getPool();
        // 1. 收集字母表, 并排序
        M[] alphabet = collectAlphabet(factory, allStates);
        // 2. 初始划分：接受状态和非接受状态。
        Partition<M, V> partition = initDepart(allStates);
        // 3. 迭代细化分区，直到不再变化。
        for (int preSize = partition.size(); ; preSize = partition.size()) {
            partition = refinePartition(partition, alphabet);
            if (preSize == partition.size()) {
                break;
            }
        }
        // 4. 构建最小化后的状态表
        return buildMinimizedTable(factory, partition, alphabet, dfaStatus.getStart());
    }

    private static class Block<M, V extends StatusVertex> extends ArrayList<DfaStatus<M, V>> implements
            List<DfaStatus<M, V>> {
        private final V accept;

        /**
         * 函数功能：创建具有指定接受标记的状态块。
         * 输入：
         * - accept：状态块对应的接受标记。
         * 输出：无。
         */
        private Block(V accept) {this.accept = accept;}

    }

    private static class Partition<M, V extends StatusVertex> extends ArrayList<Block<M, V>> implements
            List<Block<M, V>> {
        private final Map<DfaStatus<M, V>, Integer> stateToBlockIdx;

        /**
         * 函数功能：创建空的 DFA 状态分区。
         * 输入：
         * - 无。
         * 输出：无。
         */
        public Partition() {
            this.stateToBlockIdx = new HashMap<>();
        }

        /**
         * 函数功能：获取 DFA 状态所在的分区块索引。
         * 输入：
         * - target：待查询的 DFA 状态。
         * 输出：状态所在分区块索引。
         */
        public Integer getIndexByStatus(DfaStatus<M, V> target) {
            return stateToBlockIdx.get(target);
        }

        /**
         * 函数功能：添加状态块并记录其中状态的分区索引。
         * 输入：
         * - block：待添加的状态块。
         * 输出：是否添加成功的布尔值。
         */
        @Override
        public boolean add(Block<M, V> block) {
            int nextIndex = size();
            block.forEach(s -> this.putStatusIdx(s, nextIndex));
            return super.add(block);
        }

        /**
         * 函数功能：记录 DFA 状态对应的分区块索引。
         * 输入：
         * - status：待记录的 DFA 状态。
         * - index：状态所在分区块索引。
         * 输出：无。
         */
        public void putStatusIdx(DfaStatus<M, V> status, int index) {
            stateToBlockIdx.put(status, index);
        }
    }


}
