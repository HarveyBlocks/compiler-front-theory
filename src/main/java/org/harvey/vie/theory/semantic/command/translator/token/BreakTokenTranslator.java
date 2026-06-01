package org.harvey.vie.theory.semantic.command.translator.token;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.TokenCommandRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.command.translator.command.WhileStatementTranslator;

import java.util.List;

/**
 * 控制流 token 支路：移进 {@code break} 时先生成“目标未知”的 goto 命令。
 * <p>
 * 当前 token 阶段还不知道它属于哪一层循环，所以这里只把
 * {@link UncertainLabelGotoCommand} 放进 {@link TokenCommandRegister#getUncertainBreaks()}。
 * 外层 {@link WhileStatementTranslator#bindLoopLabels} 或
 * {@link org.harvey.vie.theory.semantic.command.translator.command.DoWhileStatementTranslator}
 * 之后会把它绑定到当前循环的出口标签；如果一直没人绑定，顶层
 * {@link org.harvey.vie.theory.semantic.command.translator.command.ProgramCommandTranslator} 会报错。
 * <p>
 * 讲完本支路回到 {@link WhileStatementTranslator}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class BreakTokenTranslator implements TokenTranslator {

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
