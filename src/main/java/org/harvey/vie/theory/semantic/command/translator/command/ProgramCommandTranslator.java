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
 * {@link TagStrategyCompose} 遇到 {@code PROGRAM} 标签时会进入这里。program 是整份源程序的根非终结符，
 * 所以它本身通常不新增计算命令，而是复用 {@link SimpleShrinkTranslator} 把所有顶层子项顺序拼接成一个
 * {@link NormalCommandNodeRegister}。这对应编译原理里的“自底向上规约后，父节点综合子节点属性”。
 * <p>
 * 本类额外做一次顶层语义检查：如果还有未绑定目标的 {@code break}/{@code continue}，
 * 说明这些控制流语句不在任何循环里。此时通过
 * {@link ShiftReduceSemanticContext#addError(int, String)} 把错误放入错误上下文，并抛出异常阻止生成合法结果。
 * <p>
 * 主线下一站：{@link CommandNodeRegister}。下一站会讲每个翻译器返回的“命令片段注册器”如何继续组合命令树。
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
