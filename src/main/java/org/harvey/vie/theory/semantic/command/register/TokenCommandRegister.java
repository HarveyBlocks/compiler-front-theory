package org.harvey.vie.theory.semantic.command.register;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:22
 */
@AllArgsConstructor
public class TokenCommandRegister implements CommandNodeRegister {
    private final SemanticCommand command;
    private final List<UncertainLabelGotoCommand> uncertainBreaks;
    private final List<UncertainLabelGotoCommand> uncertainContinues;
    private final SemanticType type;
    private final SemanticType instructionType;
    private final SourceToken anchorToken;

    public TokenCommandRegister(SemanticCommand command) {
        this(command, List.of(), List.of(), SemanticType.unknown(), SemanticType.unknown(), null);
    }

    public TokenCommandRegister(
            SemanticCommand command,
            SemanticType type,
            SemanticType instructionType,
            SourceToken anchorToken) {
        // TODO 为什么要在这里增加Type.unknown(), 这种unknown是极其危险的,
        //  缺少严格的检查和自洽的逻辑将极其容易导致问题
        //  说到底也是一种"补丁"的思想罢了, 看起来差不多, 就用一个unknown占位, 是一种不负责任的表现
        this(command, List.of(), List.of(), type, instructionType, anchorToken);
    }

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new TerminalNode(command));
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
}
