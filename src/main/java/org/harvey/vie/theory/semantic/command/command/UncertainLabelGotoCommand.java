package org.harvey.vie.theory.semantic.command.command;


import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 15:13
 */
public interface UncertainLabelGotoCommand extends SemanticCommand {
    /**
     * 函数功能：获取语义标签。
     * 输入：
     * - 无。
     * 输出：SemanticLabel 类型返回值。
     */
    SemanticLabel getLabel();

    /**
     * 函数功能：设置语义标签。
     * 输入：
     * - label：SemanticLabel 类型参数。
     * 输出：无。
     */
    void setLabel(SemanticLabel label);

    /**
     * 函数功能：获取词法单元。
     * 输入：
     * - 无。
     * 输出：SourceToken 类型返回值。
     */
    SourceToken getToken();

    /**
     * 函数功能：判断标签跳转是否已解析。
     * 输入：
     * - 无。
     * 输出：判断结果布尔值。
     */

    default boolean isResolved() {
        return getLabel() != null;
    }
}
