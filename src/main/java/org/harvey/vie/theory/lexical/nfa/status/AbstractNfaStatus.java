package org.harvey.vie.theory.lexical.nfa.status;

import java.util.function.Function;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 20:15
 */
public abstract class AbstractNfaStatus implements NfaStatus {
    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof NfaStatus && ((NfaStatus) obj).getId() == getId();
    }

}
