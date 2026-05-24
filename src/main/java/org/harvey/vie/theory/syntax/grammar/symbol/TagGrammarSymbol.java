package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 16:44
 */
public interface TagGrammarSymbol {
    void addTag(SemanticTag[] array);

    SemanticTag[] getTags();
}
