package org.harvey.vie.theory.lexical.nfa;

import org.harvey.vie.theory.lexical.nfa.status.NfaStatusTable;
import org.harvey.vie.theory.lexical.regex.node.RegexNode;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 10:21
 */
public interface RegexNfaAdaptor {
    NfaStatusTable adapt(RegexNode node);
}
