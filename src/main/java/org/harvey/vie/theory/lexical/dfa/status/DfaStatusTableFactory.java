package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.lexical.nfa.status.StatusVertex;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-04 19:26
 */
public interface DfaStatusTableFactory<M, V extends StatusVertex,P extends  DfaStatusTable<M,V>> {


    P produce(int[][] newStates, M[] alphabet, int newStart, V[] accepts);

    V[] newVertexArray(int length);

    M[] newMotionArray(int length);
}
