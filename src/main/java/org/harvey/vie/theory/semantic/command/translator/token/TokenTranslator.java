package org.harvey.vie.theory.semantic.command.translator.token;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * 单个 token 的命令翻译器接口。
 * <p>
 * {@link org.harvey.vie.theory.semantic.command.CommandBuildCallback} 在移进时调用它。
 * 常量 token 可以直接变成命令，控制流关键字可以先变成未绑定跳转，其他 token 则可以返回
 * {@link org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister} 等待规约阶段处理。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 21:43
 */
public interface TokenTranslator {
    CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token);
}
