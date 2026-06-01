package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 函数支路：函数头本身不生成运行时命令。
 * <p>
 * 函数名、参数和返回类型由函数语义回调登记到
 * {@link org.harvey.vie.theory.semantic.function.FunctionRecord}；
 * 真正的函数体命令收集发生在 {@link FunctionDefinitionTranslator}。
 * 讲完这里回到 {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
 *
 * @author Temper
 */
public class FunctionHeadTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        return new PlaceholderNodeRegister();
    }
}
