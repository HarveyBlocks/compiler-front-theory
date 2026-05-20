package org.harvey.vie.theory.semantic.command.register;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
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
@AllArgsConstructor
public class NormalCommandNodeRegister implements CommandNodeRegister {
    private final CommandNode[] childrenNode;
    private final SimpleGrammarProduction production;
    private final List<UncertainLabelGotoCommand> uncertainBreaks;
    private final List<UncertainLabelGotoCommand> uncertainContinues;
    private final SemanticType type;
    private final SemanticType instructionType;
    private final SourceToken anchorToken;

    public NormalCommandNodeRegister(CommandNode[] childrenNode, SimpleGrammarProduction production) {
        this(childrenNode, production, List.of(), List.of(), SemanticType.unknown(), SemanticType.unknown(), null);
    }

    public NormalCommandNodeRegister(
            CommandNode[] childrenNode,
            SimpleGrammarProduction production,
            CommandNodeRegister[] childrenRegisters) {
        this(
                childrenNode,
                production,
                collectBreaks(childrenRegisters),
                collectContinues(childrenRegisters),
                passthroughType(childrenRegisters),
                passthroughInstructionType(childrenRegisters),
                passthroughAnchor(childrenRegisters)
        );
    }

    public NormalCommandNodeRegister(
            CommandNode[] childrenNode,
            SimpleGrammarProduction production,
            CommandNodeRegister[] childrenRegisters,
            SemanticType type,
            SemanticType instructionType,
            SourceToken anchorToken) {
        this(
                childrenNode,
                production,
                collectBreaks(childrenRegisters),
                collectContinues(childrenRegisters),
                type,
                instructionType,
                anchorToken
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

    @Override
    public SemanticType getType() {
        return type;
    }

    @Override
    public SemanticType getInstructionType() {
        return instructionType;
    }

    @Override
    public SourceToken getAnchorToken() {
        return anchorToken;
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

    private static SemanticType passthroughType(CommandNodeRegister[] childrenRegisters) {
        // TODO unknown 其实就是不负责任的补丁, 毫无健壮性, 只是将错误延后了, 反而无法找出错误所在
        //  而且, 后面对于unknown的处理, 不是作为错误处理, 而是作为"不知道"消极处理
        return childrenRegisters.length == 1 ? childrenRegisters[0].getType() : SemanticType.unknown();
    }

    private static SemanticType passthroughInstructionType(CommandNodeRegister[] childrenRegisters) {
        // TODO unknown 其实就是不负责任的补丁, 毫无健壮性, 只是将错误延后了, 反而无法找出错误所在
        //  而且, 后面对于unknown的处理, 不是作为错误处理, 而是作为"不知道"消极处理
        return childrenRegisters.length == 1 ? childrenRegisters[0].getInstructionType() : SemanticType.unknown();
    }

    private static SourceToken passthroughAnchor(CommandNodeRegister[] childrenRegisters) {
        if (childrenRegisters.length == 1) {
            // TODO 真的是这样的? 还是一种妥协?
            return childrenRegisters[0].getAnchorToken();
        }
        for (CommandNodeRegister child : childrenRegisters) {
            if (child.getAnchorToken() != null) {
                return child.getAnchorToken();
            }
        }
        return null;
    }
}
