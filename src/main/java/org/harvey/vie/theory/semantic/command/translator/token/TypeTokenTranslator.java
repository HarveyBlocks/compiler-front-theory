package org.harvey.vie.theory.semantic.command.translator.token;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;

/**
 * 类型 token 的占位翻译器。
 * <p>
 * 类型关键字只参与类型推导和符号表登记，不直接对应运行时命令；真正需要按类型生成命令时，
 * 会由 {@link org.harvey.vie.theory.semantic.command.command.factory.CommandDataType} 把
 * {@link org.harvey.vie.theory.semantic.type.SemanticType} 转成命令后缀。
 * 讲完本支路回到 {@link org.harvey.vie.theory.semantic.command.CommandBuildCallback}。
 *
 * @author Temper
 */
public class TypeTokenTranslator implements TokenTranslator {
    @Override
    public CommandNodeRegister translate(ShiftReduceSemanticContext context, SourceToken token) {
        return new PlaceholderNodeRegister();
    }
}
