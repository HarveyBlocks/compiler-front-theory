package org.harvey.vie.theory.semantic.command.node;

import lombok.Getter;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
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
public class HeadNode extends IRandomAccess.ArrayImpl<CommandNode> implements
        CommandNode {
    private final SimpleGrammarProduction production;

    public HeadNode(CommandNode[] children, SimpleGrammarProduction production) {
        super(children);
        this.production = production;
    }

    @Override
    public void flat(List<SemanticCommand> result) {
        // 递归
        for (CommandNode child : this) {
            child.flat(result);
        }
    }
}
