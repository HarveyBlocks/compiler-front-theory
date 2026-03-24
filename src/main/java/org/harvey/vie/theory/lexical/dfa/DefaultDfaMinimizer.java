package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;
import org.harvey.vie.theory.source.SourceCharacter;
import org.harvey.vie.theory.util.IntArraySignature;

import java.util.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 22:10
 */
public class DefaultDfaMinimizer implements DfaMinimizer {

    private static final int UNKNOWN_CHAR_STATUS = DfaStatusTable.UNKNOWN_CHAR_STATUS;

    @Override
    public DfaStatusTable minimize(DfaStatusGraph dfaStatus) {
        Collection<DfaStatus> allStates = dfaStatus.getPool();
        // 1. 收集字母表, 并排序
        SourceCharacter[] alphabet = collectAlphabet(allStates);
        // 2. 初始划分：接受状态和非接受状态。
        Partition partition = initDepart(allStates);
        // 3. 迭代细化分区，直到不再变化。
        for (int preSize = partition.size(); ; preSize = partition.size()) {
            partition = refinePartition(partition, alphabet);
            if (preSize == partition.size()) {
                break;
            }
        }
        // 4. 构建最小化后的状态表
        return buildMinimizedTable(partition, alphabet, dfaStatus.getStart());
    }

    private static SourceCharacter[] collectAlphabet(Collection<DfaStatus> allStates) {
        return allStates.stream()
                .map(DfaStatus::motions)
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .toArray(SourceCharacter[]::new);
    }

    private static Partition initDepart(Collection<DfaStatus> allStates) {
        Block accepting = new Block();
        Block nonAccepting = new Block();
        for (DfaStatus s : allStates) {
            (s.isAccept() ? accepting : nonAccepting).add(s);
        }
        Partition partition = new Partition();
        if (!nonAccepting.isEmpty()) {
            partition.add(nonAccepting);
        }
        if (!accepting.isEmpty()) {
            partition.add(accepting);
        }
        return partition;
    }

    private static Partition refinePartition(Partition partition, SourceCharacter[] alphabet) {
        Partition newPartition = new Partition();
        for (Block block : partition) {
            if (block.size() == 1) {
                newPartition.add(block);
                continue;
            }
            // 分割
            Collection<Block> group = groupByMotions(block, alphabet, partition.stateToBlockIdx);
            //noinspection UseBulkOperation
            group.forEach(newPartition::add);
        }
        return newPartition;
    }


    private static Collection<Block> groupByMotions(
            Block block, SourceCharacter[] alphabet, Map<DfaStatus, Integer> stateToBlockIdx) {
        Map<IntArraySignature, Block> groups = new HashMap<>();
        for (DfaStatus s : block) {
            // 计算状态在字母表上的转移目标块索引的签名。
            // 签名由每个状态对应的目标块索引组合而成。
            IntArraySignature signature = computeSignature(s, alphabet, stateToBlockIdx);
            groups.computeIfAbsent(signature, k -> new Block()).add(s);
        }
        return groups.values();
    }

    private static IntArraySignature computeSignature(
            DfaStatus s, SourceCharacter[] alphabet, Map<DfaStatus, Integer> stateToBlockIdx) {
        return new IntArraySignature(Arrays.stream(alphabet)
                .map(s::move)
                .mapToInt(target -> target == null ? UNKNOWN_CHAR_STATUS : stateToBlockIdx.get(target))
                .toArray());
    }

    /**
     * 根据稳定分区构建最小化后的 DFA 状态表。
     */
    private static DfaStatusTable buildMinimizedTable(
            Partition partition, SourceCharacter[] alphabet, DfaStatus start) {
        // 每个块对应一个新状态
        int newStatesLength = partition.size();
        int[][] newStates = new int[newStatesLength][alphabet.length];
        BitSet acceptSet = new BitSet(newStatesLength);
        for (int i = 0; i < newStatesLength; i++) {
            Block block = partition.get(i);
            if (block.accept) {
                acceptSet.set(i);
            }
            DfaStatus representative = block.iterator().next();// 任选一
            for (int j = 0; j < alphabet.length; j++) {
                DfaStatus target = representative.move(alphabet[j]);
                newStates[i][j] = target == null ? UNKNOWN_CHAR_STATUS : partition.getIndexByStatus(target);
            }
        }
        // 定位新的起始状态
        int newStart = partition.getIndexByStatus(start);
        return new DfaStatusTable(newStates, alphabet, newStart, acceptSet);
    }

    private static class Block extends ArrayList<DfaStatus> implements List<DfaStatus> {
        private boolean accept;

        public void setAccept(boolean statusAccept) {
            this.accept = this.accept || statusAccept;
        }

        @Override
        public boolean add(DfaStatus dfaStatus) {
            setAccept(dfaStatus.isAccept());
            return super.add(dfaStatus);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
    }

    private static class Partition extends ArrayList<Block> implements List<Block> {
        private final Map<DfaStatus, Integer> stateToBlockIdx;

        public Partition() {
            this.stateToBlockIdx = new HashMap<>();
        }

        @Override
        public boolean add(Block block) {
            int i = size();
            block.forEach(s -> stateToBlockIdx.put(s, i));
            return super.add(block);
        }


        public Block getByStatus(DfaStatus target) {
            // 构建状态到块索引的映射，便于快速定位目标块
            return get(stateToBlockIdx.get(target));
        }

        public Integer getIndexByStatus(DfaStatus target) {
            return stateToBlockIdx.get(target);
        }
    }


}