package org.harvey.vie.theory.semantic.command.command;


import org.harvey.vie.theory.lexical.analysis.token.SourceToken;

/**
 * 目标标签暂时未知的无条件跳转命令。
 * <p>
 * {@code break}/{@code continue} 在 token 移进阶段先生成这种命令，但当时还不知道最近循环的出口或条件位置。
 * 所以它会跟随 {@link org.harvey.vie.theory.semantic.command.register.CommandNodeRegister#getUncertainBreaks()}
 * 或 {@link org.harvey.vie.theory.semantic.command.register.CommandNodeRegister#getUncertainContinues()} 向外传播，
 * 最后由 {@link org.harvey.vie.theory.semantic.command.translator.command.WhileStatementTranslator#bindLoopLabels}
 * 设置真实 {@link SemanticLabel}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 15:13
 */
public interface UncertainLabelGotoCommand extends SemanticCommand{
    void setLabel(SemanticLabel label);
    SemanticLabel getLabel();
    SourceToken getToken();

    default boolean isResolved() {
        return getLabel() != null;
    }
}
