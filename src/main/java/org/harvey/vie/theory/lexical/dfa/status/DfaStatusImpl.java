package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.source.SourceCharacter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:28
 */
public class DfaStatusImpl implements DfaStatus {
    private final boolean accept;
    private final Map<SourceCharacter, DfaStatus> next;

    public DfaStatusImpl(boolean accept) {
        this.accept = accept;
        this.next = new HashMap<>();
    }


    @Override
    public DfaStatus move(SourceCharacter motion) {
        return next.get(motion);
    }

    @Override
    public Set<SourceCharacter> motions() {
        return next.keySet();
    }

    @Override
    public boolean setNext(SourceCharacter motion, DfaStatus next) {
        DfaStatus status = this.next.get(motion);
        if (status == null) {
            this.next.put(motion, next);
            return true;
        } else if (status == next) {
            return false;
        } else {
            // 确定有限自动机, 依旧存在不确定性
            throw new IllegalStateException("Deterministic finite automaton still has nondeterministic motion");
        }
    }

    @Override
    public boolean isAccept() {
        return accept;
    }

    @Override
    public String toString() {
        // TODO to be fixed after debug
        return hashCode() % 1000 + "";
    }
}
