package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 空动作翻译器：明确表示该产生式不产生中间命令。
 * <p>
 * {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose} 会把空列表、空参数表等语义标签映射到这里。
 * 返回的 {@link PlaceholderNodeRegister} 注册时不写入任何 {@link org.harvey.vie.theory.semantic.command.node.CommandNode}。
 * 讲完这里回到 {@link SimpleShrinkTranslator} 或 {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:24
 */
public class DoNotingTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        return new PlaceholderNodeRegister();
    }
}
