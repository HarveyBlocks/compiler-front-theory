package org.harvey.vie.theory.semantic.command.node;

import lombok.Getter;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
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
public class TerminalNode extends IRandomAccess.EmptyImpl<CommandNode> implements
        CommandNode {
    private final SemanticCommand command;

    /**
     * 函数功能：创建 TerminalNode 对象。
     * 输入：
     * - command：SemanticCommand 类型参数。
     * 输出：无。
     */

    public TerminalNode(SemanticCommand command) {
        this.command = command;
    }

    /**
     * 函数功能：将命令节点展开到命令列表。
     * 输入：
     * - result：List<SemanticCommand> 类型参数。
     * 输出：无。
     */

    @Override
    public void flat(List<SemanticCommand> result) {
        result.add(command);
    }
}
