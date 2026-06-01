package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 类型支路：数组类型构造本身不产生命令。
 * <p>
 * {@code int32[]} 这类类型信息会由类型回调写入
 * {@link org.harvey.vie.theory.semantic.type.TypeContext}；命令生成阶段只有在声明、赋值、数组创建等地方
 * 需要它时，才通过 {@link org.harvey.vie.theory.semantic.command.command.factory.CommandDataType} 转成命令数据类型。
 * 讲完这里回到 {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
 *
 * @author Temper
 */
public class ArrayTypeTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        return new PlaceholderNodeRegister();
    }
}
