package org.harvey.vie.theory.semantic.command.translator.token;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.TokenCommandRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class BreakTokenTranslator implements TokenTranslator {
    /**
     * 函数功能：翻译语法节点并返回命令节点注册器。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - token：SourceToken 类型参数。
     * 输出：CommandNodeRegister 类型返回值。
     */

    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        UncertainLabelGotoCommand gotoCommand = context.getCommandFactory().gotoCommandUncertainLabel(token);
        return new TokenCommandRegister(
                gotoCommand,
                List.of(gotoCommand),
                List.of()
        );
    }
}
