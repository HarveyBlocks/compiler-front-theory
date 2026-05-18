package org.harvey.vie.theory.semantic.callback.bu;

import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 02:49
 */
@FunctionalInterface
public interface ReducePredicate {
    boolean test(SimpleGrammarProduction production);
}
