package org.harvey.vie.theory.semantic.tag;

import org.harvey.vie.theory.semantic.callback.bu.ReducePredicate;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

import java.util.Arrays;

public final class ProductionTags {
    private ProductionTags() {
    }

    public static boolean matches(SimpleGrammarProduction production, SemanticTag... expected) {
        return matches(production.getTags(), expected);
    }

    public static boolean matches(SemanticTag[] actual, SemanticTag... expected) {
        if (expected == null || expected.length == 0) {
            return true;
        }
        return Arrays.stream(expected).allMatch(item -> Arrays.binarySearch(actual, item) >= 0);
    }

    public static ReducePredicate predicate(SemanticTag... expected) {
        return production -> matches(production, expected);
    }
}
