package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.source.SourceCharacter;

import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:21
 */
public interface DfaStatus {
    /**
     * @return null if no motion in this status' follow-up
     */
    DfaStatus move(SourceCharacter motion);
    Set<SourceCharacter> motions();
    /**
     * @return true if new motion
     * @throws IllegalStateException throw if motion is exist and value is different
     */
    boolean setNext(SourceCharacter motion, DfaStatus next);
    boolean isAccept();
}
