package org.harvey.vie.theory.lexical.dfa.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-23 22:21
 */
@AllArgsConstructor
@Getter
public class DfaStatusGraph {
    private final DfaStatus start;
    private final Collection<DfaStatus> pool;
}
