package org.harvey.vie.theory.semantic.tree.node;

import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

public final class HeadNodes {
    private HeadNodes() {
    }

    /**
     * TODO TOBE DELETE 这么简单一句话, 何必呢?
     */
    public static boolean matchesTags(HeadNode node, SemanticTag... expected) {
        return node.matchTags(expected);
    }

    /**
     * TODO TOBE DELETE
     */
    public static boolean matchesArity(HeadNode node, int expected) {
        return node.size() == expected;
    }

    /**
     * TODO TOBE DELETE
     */
    public static boolean matches(HeadNode node, int arity, SemanticTag... expected) {
        return matchesArity(node, arity) && matchesTags(node, expected);
    }
}
