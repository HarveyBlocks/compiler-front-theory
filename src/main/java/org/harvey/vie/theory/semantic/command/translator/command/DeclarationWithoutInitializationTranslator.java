package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 声明支路：处理“只有声明、没有初始化”的变量声明。
 * <p>
 * 符号表登记、类型绑定已经由其他语义回调完成；单纯声明不会产生运行时求值或赋值命令，
 * 所以这里返回 {@link PlaceholderNodeRegister}。如果声明带初始化，跳到
 * {@link DeclarationWithInitializationTranslator} 看“先取地址、再求右值、最后写回”的命令结构。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
public class DeclarationWithoutInitializationTranslator implements CommandTranslator {

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production, CommandNodeRegister[] children) {
        return new PlaceholderNodeRegister();
    }
}
