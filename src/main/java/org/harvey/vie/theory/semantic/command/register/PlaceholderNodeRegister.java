package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.analysis.SemanticType;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:31
 */
public class PlaceholderNodeRegister implements CommandNodeRegister {
    private final SemanticType type;
    private final SemanticType instructionType;
    private final SourceToken anchorToken;

    public PlaceholderNodeRegister() {
        // TODO 为什么要开放这种构造器? 白白地允许外界去做unknown这种危险的操作?
        this(SemanticType.unknown(), SemanticType.unknown(), null);
    }

    public PlaceholderNodeRegister(SourceToken anchorToken) {
        this(SemanticType.unknown(), SemanticType.unknown(), anchorToken);
    }

    public PlaceholderNodeRegister(SemanticType type, SemanticType instructionType, SourceToken anchorToken) {
        this.type = type;
        this.instructionType = instructionType;
        this.anchorToken = anchorToken;
    }

    @Override
    public void register(CommandNodeBuilder outer) {
        // do nothing
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
