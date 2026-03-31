package org.harvey.vie.theory.lexical.dfa.status;

import org.harvey.vie.theory.lexical.alphabet.AlphabetCharacter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Concrete implementation of {@link DfaStatus}.
 * This class uses a map to manage deterministic transitions to next states
 * and stores the token type it accepts, if any.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:28
 */
public class DfaStatusImpl implements DfaStatus {
    private final TokenType accept;
    private final Map<AlphabetCharacter, DfaStatus> next;

    public DfaStatusImpl(TokenType accept) {
        this.accept = accept;
        this.next = new HashMap<>();
    }


    @Override
    public DfaStatus move(AlphabetCharacter motion) {
        return next.get(motion);
    }

    @Override
    public Set<AlphabetCharacter> motions() {
        return next.keySet();
    }

    @Override
    public boolean setNext(AlphabetCharacter motion, DfaStatus next) {
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
    public TokenType accept() {
        return accept;
    }

    @Override
    public String toString() {
        return "DfaStatus[" + (accept != null ? accept.hint() : "non-accepting") + ", transitions=" + next.size() + "]";
    }
}
