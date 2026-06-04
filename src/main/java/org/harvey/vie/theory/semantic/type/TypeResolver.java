package org.harvey.vie.theory.semantic.type;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * 将词法 token 映射为语义类型的解析接口。
 *
 * 作用：
 *
 * TypeResolver 把具体语言的 token 类型和通用的 SemanticType 解耦。
 * semantic/type 包只依赖这个接口，不直接依赖某个具体语言的 token 枚举。
 *
 * 例如：
 *
 * 1. CONSTANT_INTEGER 可以解析为 int32。
 * 2. TYPE_FLOAT64 可以解析为 float64。
 * 3. TYPE_IDENTIFIER 可以解析为 struct 类型。
 */
public interface TypeResolver {
    /**
     * 函数功能：解析字面量类型。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    SemanticType literalType(SourceToken token);

    /**
     * 函数功能：解析类型词法单元。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：SemanticType 类型返回值。
     */
    SemanticType typeToken(SourceToken token);
}
