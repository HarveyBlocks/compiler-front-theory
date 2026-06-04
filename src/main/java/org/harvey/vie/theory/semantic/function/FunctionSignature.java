package org.harvey.vie.theory.semantic.function;

import lombok.Getter;
import org.harvey.vie.theory.lexical.analysis.token.IdentifierKey;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.type.SemanticType;

/**
 * @author Temper
 */
@Getter
public class FunctionSignature {
    private final IdentifierKey nameKey;
    private final SourceToken nameToken;
    private final SemanticType returnType;
    private final HeadNode declarationNode;

    /**
     * 函数功能：创建 FunctionSignature 对象。
     * 输入：
     * - nameToken：SourceToken 类型参数。
     * - returnType：SemanticType 类型参数。
     * - declarationNode：HeadNode 类型参数。
     * 输出：无。
     */

    public FunctionSignature(SourceToken nameToken, SemanticType returnType, HeadNode declarationNode) {
        this.nameKey = IdentifierKey.generate(nameToken);
        this.nameToken = nameToken;
        this.returnType = returnType;
        this.declarationNode = declarationNode;
    }
}

