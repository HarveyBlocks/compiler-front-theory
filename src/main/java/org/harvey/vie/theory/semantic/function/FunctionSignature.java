package org.harvey.vie.theory.semantic.function;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

import java.nio.charset.StandardCharsets;

/**
 * @author Temper
 */
@Getter
public class FunctionSignature {
    private final IdentifierKey nameKey;
    private final SourceToken nameToken;
    private final SemanticType returnType;
    private final HeadNode declarationNode;

    public FunctionSignature(SourceToken nameToken, SemanticType returnType, HeadNode declarationNode) {
        this.nameKey = IdentifierKey.generate(nameToken);
        this.nameToken = nameToken;
        this.returnType = returnType;
        this.declarationNode = declarationNode;
    }
}

