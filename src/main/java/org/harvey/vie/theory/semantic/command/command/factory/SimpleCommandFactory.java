package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.function.FunctionRecord;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:12
 */
public interface SimpleCommandFactory {
    SemanticCommand callFunction(FunctionRecord name);

    SemanticCommand returnCommand();
}
