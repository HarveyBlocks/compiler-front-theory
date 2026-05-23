package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

public class FunctionSignature {
    private final String name;
    private final SourceToken nameToken;
    private final SemanticType returnType;
    private final HeadNode declarationNode;

    public FunctionSignature(String name, SourceToken nameToken, SemanticType returnType, HeadNode declarationNode) {
        this.name = name;
        this.nameToken = nameToken;
        this.returnType = returnType;
        this.declarationNode = declarationNode;
    }

    public String getName() {
        return name;
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
