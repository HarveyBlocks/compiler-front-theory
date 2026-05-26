package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Temper
 */
public final class CommandSegmentSupport {
    private CommandSegmentSupport() {
    }

    public static List<SemanticCommand> flatten(CommandNodeRegister register) {
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        register.register(builder);
        return flatten(builder.build());
    }

    public static List<SemanticCommand> flatten(CommandNode[] nodes) {
        List<SemanticCommand> commands = new ArrayList<>();
        for (CommandNode node : nodes) {
            node.flat(commands);
        }
        return commands;
    }
}
