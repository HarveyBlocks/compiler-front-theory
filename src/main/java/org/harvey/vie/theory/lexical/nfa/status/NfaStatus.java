package org.harvey.vie.theory.lexical.nfa.status;

import org.harvey.vie.theory.source.SourceCharacter;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 13:47
 */
public interface NfaStatus {

    NfaStatus move(SourceCharacter c);

    List<NfaStatus> moveEpsilon();

    Set<SourceCharacter> motions();

    void addEpsilonNext(NfaStatus next);

    NfaStatus computeNextIfAbsent(SourceCharacter c, Supplier<NfaStatus> supplier);

    int getId();

    boolean equals(Object obj);

    int hashCode();
}
