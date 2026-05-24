package org.harvey.vie.theory.semantic.function;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

/**
 * @author Temper
 */
@Getter
public class FunctionParameter {
    private final SourceToken nameToken;
    private final SemanticType type;
    private final HeadNode typeNode;

    public FunctionParameter(SourceToken nameToken, SemanticType type, HeadNode typeNode) {
        this.nameToken = nameToken;
        this.type = type;
        this.typeNode = typeNode;
    }

    public boolean isNamed(SourceToken token) {
        return nameToken.equals(token);
    }
}

