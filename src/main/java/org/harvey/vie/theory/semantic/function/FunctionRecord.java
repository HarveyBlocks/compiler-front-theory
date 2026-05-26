package org.harvey.vie.theory.semantic.function;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;

import java.util.List;

/**
 * @author Temper
 */
@Getter
public class FunctionRecord {
    private final int tableIndex;
    private final FunctionSignature signature;
    private final List<FunctionParameter> parameters;
    private final HeadNode functionHeadNode;

    public FunctionRecord(
            int tableIndex,
            FunctionSignature signature,
            List<FunctionParameter> parameters,
            HeadNode functionHeadNode) {
        this.tableIndex = tableIndex;
        this.signature = signature;
        this.parameters = List.copyOf(parameters);
        this.functionHeadNode = functionHeadNode;
    }


    public IdentifierKey getNameKey() {
        return signature.getNameKey();
    }

    public HeadNode getDeclarationNode() {
        return signature.getDeclarationNode();
    }
}

