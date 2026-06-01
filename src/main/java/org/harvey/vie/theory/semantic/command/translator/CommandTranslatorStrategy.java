package org.harvey.vie.theory.semantic.command.translator;

import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;
import org.harvey.vie.theory.semantic.tag.TagStrategyCompose;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 规约翻译器选择策略。
 * <p>
 * {@link org.harvey.vie.theory.semantic.command.CommandBuildCallback} 在每次 reduce 时调用
 * {@link #get(SimpleGrammarProduction)}，用当前产生式取得一个 {@link CommandTranslator}。
 * 当前项目的主要实现来自 {@link TagStrategyCompose#preciseStringCommand()}，也就是按产生式语义标签选择翻译器。
 * <p>
 * 讲解主线继续看 {@link TagStrategyCompose}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:30
 */
public interface CommandTranslatorStrategy {
    CommandTranslator get(SimpleGrammarProduction production);
}
