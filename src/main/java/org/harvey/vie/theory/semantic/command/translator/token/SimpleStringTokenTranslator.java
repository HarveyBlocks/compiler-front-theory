package org.harvey.vie.theory.semantic.command.translator.token;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.command.factory.CommandFactory;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.TokenCommandRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * TODO 简单地把 token 转换成字符串
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:21
 */
public class SimpleStringTokenTranslator implements TokenTranslator {
/**
 * 函数功能：翻译语法节点并返回命令节点注册器。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - token：SourceToken 类型参数。
 * 输出：CommandNodeRegister 类型返回值。
 */

    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        return new TokenCommandRegister(context.getCommandFactory().loadStatic(token));
    }
}
