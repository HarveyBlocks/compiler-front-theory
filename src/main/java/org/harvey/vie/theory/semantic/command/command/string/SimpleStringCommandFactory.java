package org.harvey.vie.theory.semantic.command.command.string;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.factory.SimpleCommandFactory;
import org.harvey.vie.theory.semantic.function.FunctionRecord;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:13
 */
public class SimpleStringCommandFactory implements SimpleCommandFactory {
    @Override
    public SemanticCommand callFunction(FunctionRecord name) {
        return new StringCommand("call " + name.getTableIndex());
    }

    @Override
    public SemanticCommand returnCommand() {
        return new StringCommand("return");
    }
}
