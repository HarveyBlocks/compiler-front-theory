package org.harvey.vie.theory.semantic.structure;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;

/**
 * Field metadata for a declared struct type.
 *
 * @author Temper
 */
@Getter
public class StructField {
    private final SourceToken nameToken;
    private final IdentifierKey nameKey;
    private final SemanticType type;
    private final int offset;

    public StructField(SourceToken nameToken, SemanticType type, int offset) {
        this.nameToken = nameToken;
        this.nameKey = IdentifierKey.generate(nameToken);
        this.type = type;
        this.offset = offset;
    }

    public boolean isNamed(SourceToken token) {
        return nameKey.equals(IdentifierKey.generate(token));
    }
}
