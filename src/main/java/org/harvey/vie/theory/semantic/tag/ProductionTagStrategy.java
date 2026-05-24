package org.harvey.vie.theory.semantic.tag;

import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

import java.util.ArrayList;
import java.util.List;

public class ProductionTagStrategy<T> {
    private final List<Rule<T>> rules = new ArrayList<>();
    private final T defaultValue;

    public ProductionTagStrategy(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ProductionTagStrategy<T> when(T value, SemanticTag... expected) {
        rules.add(new Rule<>(value, expected));
        return this;
    }

    public T resolve(SimpleGrammarProduction production) {
        // 有点暴力
        for (Rule<T> rule : rules) {
            if (rule.matches(production)) {
                return rule.value;
            }
        }
        return defaultValue;
    }

    private static final class Rule<T> {
        private final T value;
        private final SemanticTag[] expected;

        private Rule(T value, SemanticTag[] expected) {
            this.value = value;
            this.expected = expected;
        }

        private boolean matches(SimpleGrammarProduction production) {
            return production.matchTags(expected);
        }
    }
}
