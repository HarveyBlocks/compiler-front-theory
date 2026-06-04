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
/**
 * 函数功能：创建 FunctionRecord 对象。
 * 输入：
 * - tableIndex：int 类型参数。
 * - signature：FunctionSignature 类型参数。
 * - parameters：List<FunctionParameter> 类型参数。
 * - functionHeadNode：HeadNode 类型参数。
 * 输出：无。
 */

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
/**
 * 函数功能：获取名称标识键。
 * 输入：
 * - 无。
 * 输出：IdentifierKey 类型返回值。
 */


    public IdentifierKey getNameKey() {
        return signature.getNameKey();
    }
/**
 * 函数功能：获取声明节点。
 * 输入：
 * - 无。
 * 输出：HeadNode 类型返回值。
 */

    public HeadNode getDeclarationNode() {
        return signature.getDeclarationNode();
    }
}

