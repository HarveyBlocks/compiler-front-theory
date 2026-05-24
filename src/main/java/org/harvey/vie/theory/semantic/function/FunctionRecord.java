package org.harvey.vie.theory.semantic.function;

import org.harvey.vie.theory.semantic.tree.node.HeadNode;

import java.util.List;

public class FunctionRecord {
    private final FunctionSignature signature;
    private final List<FunctionParameter> parameters;
    private final HeadNode functionHeadNode;

    public FunctionRecord(
            FunctionSignature signature,
            List<FunctionParameter> parameters,
            HeadNode functionHeadNode) {
        this.signature = signature;
        this.parameters = List.copyOf(parameters);
        this.functionHeadNode = functionHeadNode;
    }

    public String getName() {
        return signature.getName();
    }

    public FunctionSignature getSignature() {
        return signature;
    }

    public List<FunctionParameter> getParameters() {
        return parameters;
    }

    public HeadNode getFunctionHeadNode() {
        return functionHeadNode;
    }

    public HeadNode getDeclarationNode() {
        return signature.getDeclarationNode();
    }
}
