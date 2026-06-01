package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tag.TagStrategyCompose;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 讲解主线第 3 站：程序顶层翻译器。
 * <p>
 * {@link TagStrategyCompose} 遇到 {@code PROGRAM} 标签时会进入这里。程序顶层本身不新增计算命令，
 * 所以正常情况下复用 {@link SimpleShrinkTranslator} 把所有顶层子项顺序拼成一个
 * {@link NormalCommandNodeRegister}；它额外承担一个职责：检查有没有从内层漏出来、还没有被循环绑定的
 * {@code break}/{@code continue}。
 * <p>
 * 如果这些跳转还没有目标，说明它们不在任何 {@code while}/{@code do-while} 中，本类会把错误写入
 * {@link ShiftReduceSemanticContext#addError(int, String)} 并抛出异常。讲完这里，回到
 * {@link TagStrategyCompose} 看各个语句支路；支路最终汇总到 {@link CommandNodeRegister}。
 */
public class ProgramCommandTranslator implements CommandTranslator {
    private final CommandTranslator delegate = new SimpleShrinkTranslator();

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
     * 顶层语句不允许残留未解析的 break/continue。
     * <p>
     * 这些命令由 {@link org.harvey.vie.theory.semantic.command.translator.token.BreakTokenTranslator}
     * 和 {@link org.harvey.vie.theory.semantic.command.translator.token.ContinueTokenTranslator} 先创建，
     * 正常情况下会由 {@link WhileStatementTranslator#bindLoopLabels(CommandNodeRegister,
     * org.harvey.vie.theory.semantic.command.command.SemanticLabel,
     * org.harvey.vie.theory.semantic.command.command.SemanticLabel)} 绑定。到程序顶层还没有绑定，就说明写在循环外。
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
