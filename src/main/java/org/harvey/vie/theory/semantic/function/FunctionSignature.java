package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

import java.nio.charset.StandardCharsets;

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


    public IdentifierKey getNameKey() {
        return nameKey;
    }

    public SourceToken getNameToken() {
        return nameToken;
    }

    public SemanticType getReturnType() {
        return returnType;
    }

    public HeadNode getDeclarationNode() {
        return declarationNode;
    }
}
