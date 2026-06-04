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
/**
 * 函数功能：创建 FunctionParameter 对象。
 * 输入：
 * - nameToken：SourceToken 类型参数。
 * - type：SemanticType 类型参数。
 * - typeNode：HeadNode 类型参数。
 * 输出：无。
 */

    public FunctionParameter(SourceToken nameToken, SemanticType type, HeadNode typeNode) {
        this.nameToken = nameToken;
        this.type = type;
        this.typeNode = typeNode;
    }
/**
 * 函数功能：判断类型是否为命名类型。
 * 输入：
 * - token：SourceToken 类型参数。
 * 输出：判断结果布尔值。
 */

    public boolean isNamed(SourceToken token) {
        return nameToken.equals(token);
    }
}

