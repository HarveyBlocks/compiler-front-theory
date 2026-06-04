package org.harvey.vie.theory.semantic.value;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * 将常量 token 解析为具体 Java 值的接口。
 * <p>
 * 作用：
 * <p>
 * ConstantResolver 把“如何从 token 中取出常量值”的细节交给具体语言实现。
 * semantic/value 包只依赖这个接口，不直接依赖某个 token 的字符串存储格式。
 * <p>
 * 当前接口只暴露 integerLiteral，是因为数组维度等场景需要读取整数常量。
 * 更一般的表达式常量折叠目前在 ConstantValueBuildCallback 中直接根据词素解析。
 */
public interface ConstantResolver {
    /**
     * 函数功能：解析整数字面量。
     * 输入：
     * - token：SourceToken 类型参数。
     * 输出：整数结果。
     */
    int integerLiteral(SourceToken token);
}
