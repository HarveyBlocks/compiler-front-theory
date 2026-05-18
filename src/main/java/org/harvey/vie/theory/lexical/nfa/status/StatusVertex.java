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
    static <V extends StatusVertex> V morePriority(V accept, V tryAccept) {
        return accept.getPriority() < tryAccept.getPriority() ? accept : tryAccept;
    }

    /**
     * value smaller, priority higher
     */
    int getPriority();


    @NonNull
    String hint();
}
