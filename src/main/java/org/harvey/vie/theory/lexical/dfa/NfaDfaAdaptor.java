package org.harvey.vie.theory.lexical.dfa;

import org.harvey.vie.theory.lexical.dfa.status.DfaStatusGraph;
import org.harvey.vie.theory.lexical.nfa.status.NfaStatusTable;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 15:24
 */
public interface NfaDfaAdaptor {
    DfaStatusGraph adapt(NfaStatusTable table);
}
