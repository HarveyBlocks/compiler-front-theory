package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 19:26
 */
public interface DfaStatusTableFactory<M, V extends StatusVertex, P extends DfaStatusTable<M, V>> {


    /**
     * 函数功能：根据状态转移数据生产 DFA 状态表。
     * 输入：
     * - newStates：状态转移矩阵。
     * - alphabet：字母表数组。
     * - newStart：起始状态编号。
     * - accepts：状态接受标记数组。
     * 输出：生产得到的 DFA 状态表。
     */
    P produce(int[][] newStates, M[] alphabet, int newStart, V[] accepts);

    /**
     * 函数功能：创建指定长度的接受标记数组。
     * 输入：
     * - length：数组长度。
     * 输出：接受标记数组。
     */
    V[] newVertexArray(int length);

    /**
     * 函数功能：创建指定长度的转移动作数组。
     * 输入：
     * - length：数组长度。
     * 输出：转移动作数组。
     */
    M[] newMotionArray(int length);
}
