package org.harvey.vie.theory.lexical.nfa.status;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 17:41
 */
@Getter
public class DefaultNfaStatusTable implements NfaStatusTable {

    private final NfaStatus start;
    private final NfaStatus end;

    public DefaultNfaStatusTable(NfaStatus start, NfaStatus end) {
        this.start = start;
        this.end = end;
    }


}
