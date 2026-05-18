package org.harvey.vie.theory.lexical;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

import java.util.function.Predicate;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-10 14:14
 */
@FunctionalInterface
public interface TokenFilterPredict extends Predicate<SourceToken> {
    /**
     * @return true for retain, false for give up.
     */
    @Override
    boolean test(SourceToken token);
}
