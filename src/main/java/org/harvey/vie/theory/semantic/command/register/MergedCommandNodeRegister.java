package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergedCommandNodeRegister implements CommandNodeRegister {
    private final CommandNodeRegister primary;
    private final List<CommandNodeRegister> extras;

    public MergedCommandNodeRegister(CommandNodeRegister primary, CommandNodeRegister... extras) {
        this.primary = primary;
        this.extras = Arrays.asList(extras);
    }

    @Override
    public void register(CommandNodeBuilder outer) {
        primary.register(outer);
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return merge(primary.getUncertainBreaks(), true);
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainContinues() {
        return merge(primary.getUncertainContinues(), false);
    }

    private List<UncertainLabelGotoCommand> merge(List<UncertainLabelGotoCommand> seed, boolean breaks) {
        List<UncertainLabelGotoCommand> result = new ArrayList<>(seed);
        for (CommandNodeRegister extra : extras) {
            result.addAll(breaks ? extra.getUncertainBreaks() : extra.getUncertainContinues());
        }
        return result;
    }
}
