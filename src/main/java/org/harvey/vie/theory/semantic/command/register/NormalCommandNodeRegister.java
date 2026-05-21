package org.harvey.vie.theory.semantic.command.register;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.HeadNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:13
 */
public class NormalCommandNodeRegister implements CommandNodeRegister {
    private final CommandNode[] childrenNode;
    private final SimpleGrammarProduction production;
    private final List<UncertainLabelGotoCommand> uncertainBreaks;
    private final List<UncertainLabelGotoCommand> uncertainContinues;

    public NormalCommandNodeRegister(
            CommandNode[] childrenNode,
            SimpleGrammarProduction production,
            List<UncertainLabelGotoCommand> uncertainBreaks,
            List<UncertainLabelGotoCommand> uncertainContinues) {
        this.childrenNode = childrenNode;
        this.production = production;
        this.uncertainBreaks = uncertainBreaks;
        this.uncertainContinues = uncertainContinues;
    }

    public NormalCommandNodeRegister(CommandNode[] childrenNode, SimpleGrammarProduction production) {
        this(childrenNode, production, List.of(), List.of());
    }

    public NormalCommandNodeRegister(
            CommandNode[] childrenNode,
            SimpleGrammarProduction production,
            CommandNodeRegister[] childrenRegisters) {
        this(
                childrenNode,
                production,
                collectBreaks(childrenRegisters),
                collectContinues(childrenRegisters)
        );
    }

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new HeadNode(childrenNode, production));
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return uncertainBreaks;
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainContinues() {
        return uncertainContinues;
    }

    private static List<UncertainLabelGotoCommand> collectBreaks(CommandNodeRegister[] childrenRegisters) {
        List<UncertainLabelGotoCommand> result = new ArrayList<>();
        for (CommandNodeRegister child : childrenRegisters) {
            result.addAll(child.getUncertainBreaks());
        }
        return result;
    }

    private static List<UncertainLabelGotoCommand> collectContinues(CommandNodeRegister[] childrenRegisters) {
        List<UncertainLabelGotoCommand> result = new ArrayList<>();
        for (CommandNodeRegister child : childrenRegisters) {
            result.addAll(child.getUncertainContinues());
        }
        return result;
    }
}
