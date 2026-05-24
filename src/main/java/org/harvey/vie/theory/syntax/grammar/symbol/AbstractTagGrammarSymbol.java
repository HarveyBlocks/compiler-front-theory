package org.harvey.vie.theory.syntax.grammar.symbol;

import org.harvey.vie.theory.syntax.grammar.tag.SemanticTag;

import java.util.*;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 16:40
 */
public abstract class AbstractTagGrammarSymbol implements TagGrammarSymbol {
    private final Set<SemanticTag> tags;

    protected AbstractTagGrammarSymbol(Set<SemanticTag> tags) {this.tags = tags;}

    protected AbstractTagGrammarSymbol() {this(new HashSet<>());}

    @Override
    public void addTag(SemanticTag[] array) {
        tags.addAll(Arrays.asList(array));
    }

    @Override
    public SemanticTag[] getTags() {
        return tags.toArray(SemanticTag[]::new);
    }
}
