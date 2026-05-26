package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.function.FunctionRecord;

import java.util.List;

/**
 * @author Temper
 */
public class FunctionCommandSegment {
    private final FunctionRecord function;
    private final List<SemanticCommand> commands;

    public FunctionCommandSegment(FunctionRecord function, List<SemanticCommand> commands) {
        this.function = function;
        this.commands = List.copyOf(commands);
    }

    public FunctionRecord getFunction() {
        return function;
    }

    public List<SemanticCommand> getCommands() {
        return commands;
    }
}
