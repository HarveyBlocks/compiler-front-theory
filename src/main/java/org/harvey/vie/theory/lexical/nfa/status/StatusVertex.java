package org.harvey.vie.theory.lexical.nfa.status;

import lombok.NonNull;

/**
 * TODO 顶点
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 19:14
 */
public interface StatusVertex {
    /**
     * 函数功能：从两个接受顶点中选择优先级更高的顶点。
     * 输入：
     * - accept：当前接受顶点。
     * - tryAccept：待比较的接受顶点。
     * 输出：优先级更高的接受顶点。
     */
    static <V extends StatusVertex> V morePriority(V accept, V tryAccept) {
        return accept.getPriority() < tryAccept.getPriority() ? accept : tryAccept;
    }

    /**
     * 函数功能：获取当前顶点的优先级。
     * 输入：
     * - 无。
     * 输出：当前顶点的优先级数值。
     */
    int getPriority();


    /**
     * 函数功能：获取当前顶点的提示文本。
     * 输入：
     * - 无。
     * 输出：当前顶点的提示文本。
     */
    @NonNull
    String hint();
}
