package org.harvey.vie.theory.semantic.command.node;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.util.IRandomAccess;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:29
 */
@Getter
@Setter
public class LabelNode extends IRandomAccess.EmptyImpl<CommandNode> implements
        CommandNode {
    private final SemanticLabel label;

    public LabelNode(SemanticLabel label) {this.label = label;}

    @Override
    public void flat(List<SemanticCommand> result) {
        label.setIndex(result.size());
    }
}
