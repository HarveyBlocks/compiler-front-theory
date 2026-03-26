package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatus;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.dfa.status.DfaStatusTable;
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
    private static final int UNKNOWN_CHAR_STATUS = DfaStatusTable.UNKNOWN_CHAR_STATUS;

    @Override
    public DfaStatusTable minimize(DfaStatusGraph dfaStatus) {
        Collection<DfaStatus> allStates = dfaStatus.getPool();
        // 1. 收集字母表, 并排序
        AlphabetCharacter[] alphabet = collectAlphabet(allStates);
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

    private static AlphabetCharacter[] collectAlphabet(Collection<DfaStatus> allStates) {
        return allStates.stream()
                .map(DfaStatus::motions)
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .toArray(AlphabetCharacter[]::new);
    }

    private static Partition initDepart(Collection<DfaStatus> allStates) {
        Map<TokenType, Integer> typeIndexMap = new HashMap<>();
        Partition partition = new Partition();
        // 1. non accept
        Block nonAccepting = new Block(null);
        // 2. some different accept
        for (DfaStatus s : allStates) {
            TokenType accept = s.accept();
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
            Block block = new Block(accept);
            block.add(s);
            partition.add(block);
        }
        if (!nonAccepting.isEmpty()) {
            partition.add(nonAccepting);
        }
        return partition;
    }

    private static Partition refinePartition(Partition partition, AlphabetCharacter[] alphabet) {
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
            Block block, AlphabetCharacter[] alphabet, Map<DfaStatus, Integer> stateToBlockIdx) {
        Map<IntArraySignature, Block> groups = new HashMap<>();
        for (DfaStatus s : block) {
            // 计算状态在字母表上的转移目标块索引的签名。
            // 签名由每个状态对应的目标块索引组合而成。
            IntArraySignature signature = computeSignature(s, alphabet, stateToBlockIdx);
            groups.computeIfAbsent(signature, k -> new Block(block.accept)).add(s);
        }
        return groups.values();
    }

    private static IntArraySignature computeSignature(
            DfaStatus s, AlphabetCharacter[] alphabet, Map<DfaStatus, Integer> stateToBlockIdx) {
        return new IntArraySignature(Arrays.stream(alphabet)
                .map(s::move)
                .mapToInt(target -> target == null ? UNKNOWN_CHAR_STATUS : stateToBlockIdx.get(target))
                .toArray());
    }

    /**
     * 根据稳定分区构建最小化后的 DFA 状态表。
     */
    private static DfaStatusTable buildMinimizedTable(
            Partition partition, AlphabetCharacter[] alphabet, DfaStatus start) {
        // 每个块对应一个新状态
        int newStatesLength = partition.size();
        int[][] newStates = new int[newStatesLength][alphabet.length];
        TokenType[] accepts = new TokenType[newStatesLength];
        for (int i = 0; i < newStatesLength; i++) {
            Block block = partition.get(i);
            accepts[i] = block.accept; // null 就是 null
            DfaStatus representative = block.iterator().next();// 任选一
            for (int j = 0; j < alphabet.length; j++) {
                DfaStatus target = representative.move(alphabet[j]);
                newStates[i][j] = target == null ? UNKNOWN_CHAR_STATUS : partition.getIndexByStatus(target);
            }
        }
        // 定位新的起始状态
        int newStart = partition.getIndexByStatus(start);
        return new DfaStatusTable(newStates, alphabet, newStart, accepts);
    }

    private static class Block extends ArrayList<DfaStatus> implements List<DfaStatus> {
        private final TokenType accept;

        private Block(TokenType accept) {this.accept = accept;}

    }

    private static class Partition extends ArrayList<Block> implements List<Block> {
        private final Map<DfaStatus, Integer> stateToBlockIdx;

        public Partition() {
            this.stateToBlockIdx = new HashMap<>();
        }

        public Integer getIndexByStatus(DfaStatus target) {
            return stateToBlockIdx.get(target);
        }

        @Override
        public boolean add(Block block) {
            int nextIndex = size();
            block.forEach(s -> this.putStatusIdx(s, nextIndex));
            return super.add(block);
        }

        public void putStatusIdx(DfaStatus status, int index) {
            stateToBlockIdx.put(status, index);
        }
    }


}