package org.harvey.vie.theory.semantic.command.translator;

import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:30
 */
public interface CommandTranslatorStrategy {
    /**
     * 函数功能：获取指定键或索引对应的对象。
     * 输入：
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：CommandTranslator 类型返回值。
     */
    CommandTranslator get(SimpleGrammarProduction production);
}
