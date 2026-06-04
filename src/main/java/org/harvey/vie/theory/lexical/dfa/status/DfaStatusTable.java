package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 19:26
 */
public interface DfaStatusTable<M, V extends StatusVertex> {
    /**
     * 函数功能：根据当前状态和动作计算下一状态编号。
     * 输入：
     * - statusNow：当前状态编号。
     * - motion：状态转移动作。
     * 输出：下一状态编号。
     */
    int move(int statusNow, M motion);

    /**
     * 函数功能：获取指定状态的接受标记。
     * 输入：
     * - i：状态编号。
     * 输出：接受标记；非接受状态返回 null。
     */
    V accept(int i);

    /**
     * 函数功能：获取起始状态编号。
     * 输入：
     * - 无。
     * 输出：起始状态编号整数。
     */
    int getStart();
}
