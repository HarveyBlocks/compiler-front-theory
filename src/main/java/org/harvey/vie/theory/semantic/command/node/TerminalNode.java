package org.harvey.vie.theory.semantic.command.node;

import lombok.Getter;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.util.IRandomAccess;

import java.util.List;

/**
 * 终结命令节点：真正持有一条 {@link SemanticCommand}。
 * <p>
 * {@link org.harvey.vie.theory.semantic.command.register.TokenCommandRegister} 会把 token 或翻译器创建出的命令包装成
 * {@link TerminalNode}。线性展开时，{@link #flat(List)} 直接把命令追加到结果列表，所以它会占用一个实际命令下标。
 * <p>
 * 讲完本类可看不占命令下标、只记录跳转目标的 {@link LabelNode}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:29
 */
@Getter
public class TerminalNode extends IRandomAccess.EmptyImpl<CommandNode> implements
        CommandNode {
    private final SemanticCommand command;

    public TerminalNode(SemanticCommand command) {
        this.command = command;
    }

    @Override
    public void flat(List<SemanticCommand> result) {
        result.add(command);
    }
}
