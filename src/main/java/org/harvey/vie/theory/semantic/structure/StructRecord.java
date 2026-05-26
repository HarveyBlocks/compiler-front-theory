package org.harvey.vie.theory.semantic.structure;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

import java.util.List;

/**
 * Declared struct type with ordered field layout.
 *
 * @author Temper
 */
@Getter
public class StructRecord {
    private final SourceToken nameToken;
    private final IdentifierKey nameKey;
    private final List<StructField> fields;
    private final HeadNode declarationNode;

    public StructRecord(SourceToken nameToken, List<StructField> fields, HeadNode declarationNode) {
        this.nameToken = nameToken;
        this.nameKey = IdentifierKey.generate(nameToken);
        this.fields = List.copyOf(fields);
        this.declarationNode = declarationNode;
    }

    public StructField field(SourceToken token) {
        for (StructField field : fields) {
            if (field.isNamed(token)) {
                return field;
            }
        }
        return null;
    }
}
