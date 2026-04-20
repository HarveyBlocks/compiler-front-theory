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

    private static <M, V extends StatusVertex, P extends DfaStatusTable<M, V>> M[] collectAlphabet(
            DfaStatusTableFactory<M, V, P> factory, Collection<DfaStatus<M, V>> allStates) {
        return allStates.stream()
                .map(DfaStatus::motions)
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .toArray(factory::newMotionArray);
    }

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

    private static <M, V extends StatusVertex> IntArraySignature computeSignature(
            DfaStatus<M, V> s, M[] alphabet, Map<DfaStatus<M, V>, Integer> stateToBlockIdx) {
        return new IntArraySignature(Arrays.stream(alphabet)
                .map(s::move)
                .mapToInt(target -> target == null ? UNKNOWN_MOVE_STATUS : stateToBlockIdx.get(target))
                .toArray());
    }

    /**
     * 根据稳定分区构建最小化后的 DFA 状态表。
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

        private Block(V accept) {this.accept = accept;}

    }

    private static class Partition<M, V extends StatusVertex> extends ArrayList<Block<M, V>> implements
            List<Block<M, V>> {
        private final Map<DfaStatus<M, V>, Integer> stateToBlockIdx;

        public Partition() {
            this.stateToBlockIdx = new HashMap<>();
        }

        public Integer getIndexByStatus(DfaStatus<M, V> target) {
            return stateToBlockIdx.get(target);
        }

        @Override
        public boolean add(Block<M, V> block) {
            int nextIndex = size();
            block.forEach(s -> this.putStatusIdx(s, nextIndex));
            return super.add(block);
        }

        public void putStatusIdx(DfaStatus<M, V> status, int index) {
            stateToBlockIdx.put(status, index);
        }
    }


}