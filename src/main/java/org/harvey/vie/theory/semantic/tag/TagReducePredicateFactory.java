package org.harvey.vie.theory.semantic.tag;

import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 19:59
 */
public final class TagReducePredicateFactory {
    private TagReducePredicateFactory() {
    }

    public static ReducePredicate predicate(SemanticTag... expected) {
        return production -> production.matchTags(expected);
    }
}
