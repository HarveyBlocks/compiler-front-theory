package org.harvey.vie.theory.semantic.command.translator.token;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.TokenCommandRegister;
import org.harvey.vie.theory.semantic.command.translator.command.DoWhileStatementTranslator;
import org.harvey.vie.theory.semantic.command.translator.command.WhileStatementTranslator;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

import java.util.List;

/**
 * 控制流 token 支路：移进 {@code continue} 时先生成“目标未知”的 goto 命令。
 * <p>
 * 当前 token 阶段还不知道最近的循环是哪一个，所以这里只把
 * {@link UncertainLabelGotoCommand} 放进 {@link TokenCommandRegister#getUncertainContinues()}。
 * {@link WhileStatementTranslator} 会把它绑定到 while 的条件起点；
 * {@link DoWhileStatementTranslator} 会把它绑定到 do-while 的条件检测点。
 * 如果一直没人绑定，{@link org.harvey.vie.theory.semantic.command.translator.command.ProgramCommandTranslator}
 * 会在程序顶层报错。
 * <p>
 * 讲完本支路回到 {@link WhileStatementTranslator}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class ContinueTokenTranslator implements TokenTranslator {

    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        UncertainLabelGotoCommand gotoCommand = context.getCommandFactory().gotoCommandUncertainLabel(token);
        return new TokenCommandRegister(
                gotoCommand,
                List.of(),
                List.of(gotoCommand)
        );
    }
}
