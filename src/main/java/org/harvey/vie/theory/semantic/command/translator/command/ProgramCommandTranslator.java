package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 程序顶层命令翻译器。
 * <p>
 * 它复用普通的顺序收缩逻辑来拼接子节点命令，
 * 但会在最外层额外兜底检查未绑定的 break/continue，
 * 防止非法跳转从语句块中泄漏到程序级别。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 15:29
 */
public class ProgramCommandTranslator implements CommandTranslator {
    private final CommandTranslator delegate = new SimpleShrinkTranslator();

    /**
     * 函数功能：翻译语法节点并返回命令节点注册器。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * - children：CommandNodeRegister[] 类型参数。
     * 输出：CommandNodeRegister 类型返回值。
     */

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        CommandNodeRegister result = delegate.translate(context, production, children);
        rejectUnresolved(context, result);
        return result;
    }

    /**
     * 函数功能：拒绝未解析的跳转命令。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - result：CommandNodeRegister 类型参数。
     * 输出：无。
     */
    private void rejectUnresolved(ShiftReduceSemanticContext context, CommandNodeRegister result) {
        boolean failed = false;
        for (UncertainLabelGotoCommand gotoCommand : result.getUncertainBreaks()) {
            if (!gotoCommand.isResolved()) {
                context.addError(gotoCommand.getToken().getOffset(), "break is not allowed here.");
                failed = true;
            }
        }
        for (UncertainLabelGotoCommand gotoCommand : result.getUncertainContinues()) {
            if (!gotoCommand.isResolved()) {
                context.addError(gotoCommand.getToken().getOffset(), "continue is not allowed here.");
                failed = true;
            }
        }
        if (failed) {
            throw new CompilerException("break/continue is not allowed here.");
        }
    }
}
