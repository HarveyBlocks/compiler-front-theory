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
    /**
     * 函数功能：创建 CommandSegmentSupport 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private CommandSegmentSupport() {
    }

    /**
     * 函数功能：展开命令节点并返回命令列表。
     * 输入：
     * - register：CommandNodeRegister 类型参数。
     * 输出：List<SemanticCommand> 类型集合或迭代结果。
     */

    public static List<SemanticCommand> flatten(CommandNodeRegister register) {
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        register.register(builder);
        return flatten(builder.build());
    }

    /**
     * 函数功能：展开命令节点并返回命令列表。
     * 输入：
     * - nodes：CommandNode[] 类型参数。
     * 输出：List<SemanticCommand> 类型集合或迭代结果。
     */

    public static List<SemanticCommand> flatten(CommandNode[] nodes) {
        List<SemanticCommand> commands = new ArrayList<>();
        for (CommandNode node : nodes) {
            node.flat(commands);
        }
        return commands;
    }
}
