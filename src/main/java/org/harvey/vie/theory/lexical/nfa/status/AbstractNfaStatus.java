package org.harvey.vie.theory.lexical.nfa.status;

/**
 * Abstract base class for {@link NfaStatus} implementations.
 * Provides log identity logic using the state's unique ID.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 20:15
 */
public abstract class AbstractNfaStatus<M> implements NfaStatus<M> {
    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NfaStatus && ((NfaStatus<?>) obj).getId() == getId();
    }

}
