package org.harvey.vie.theory.lexical.nfa.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.TokenType;

import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 17:41
 */
@Getter
@AllArgsConstructor
public class DefaultNfaStatusGraph implements NfaStatusGraph {
    private final NfaStatus start;
    private final Map<NfaStatus, TokenType> ends;

    @Override
    public TokenType matchAccept(NfaStatus status) {
        return ends.get(status);
    }
}
