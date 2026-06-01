package org.harvey.vie.theory.semantic.command.translator.token;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.TokenCommandRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * 常量 token 翻译器：把字面量直接翻译成一条装载静态值的命令。
 * <p>
 * 这里通过 {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#loadStatic(SourceToken)}
 * 生成 {@code load_st_*_static ...}，例如整数常量会变成 {@code load_st_int32_static 1}。
 * 讲完本支路回到 {@link org.harvey.vie.theory.semantic.command.CommandBuildCallback} 的移进流程。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:21
 */
public class SimpleStringTokenTranslator implements TokenTranslator {

    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        return new TokenCommandRegister(context.getCommandFactory().loadStatic(token));
    }
}
