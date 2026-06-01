package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.CommandBuildCallback;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 单个语法规约规则的命令翻译器接口。
 * <p>
 * {@link CommandBuildCallback} 会把右部每个符号已经生成的 {@link CommandNodeRegister} 按原顺序传进来。
 * 实现类负责决定是简单拼接这些子命令，还是额外插入赋值、运算、跳转、标签、函数调用等命令。
 * 返回值仍是 {@link CommandNodeRegister}，这样外层产生式可以继续组合。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@FunctionalInterface
public interface CommandTranslator {
    CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children);
}
