package org.harvey.vie.theory.semantic.command.translator;

import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:30
 */
public interface CommandTranslatorStrategy {
    CommandTranslator get(int productionId);
}
