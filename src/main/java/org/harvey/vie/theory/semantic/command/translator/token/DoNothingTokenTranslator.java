package org.harvey.vie.theory.semantic.command.translator.token;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * 默认 token 翻译器：该 token 对中间命令没有直接贡献。
 * <p>
 * 例如括号、分号、类型关键字等 token 通常只参与语法结构或类型信息，不直接产生命令文本。
 * 它们返回 {@link PlaceholderNodeRegister}，等外层产生式规约时由对应的
 * {@link org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator} 决定是否需要命令。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 23:07
 */
public class DoNothingTokenTranslator implements TokenTranslator {
    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        return new PlaceholderNodeRegister();
    }


}
