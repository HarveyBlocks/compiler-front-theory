package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * 讲解主线第 7 站：把命令树展开成线性命令段。
 * <p>
 * 前面所有翻译器返回的都是 {@link CommandNodeRegister}，内部可能还是树形结构。
 * 本工具先让注册器把节点写入 {@link CommandNodeListBuilder}，再从左到右调用
 * {@link CommandNode#flat(List)}。展开结果就是后续保存和打印的
 * {@link SemanticCommand} 列表。
 * <p>
 * 标签解析也发生在这个过程中：{@link org.harvey.vie.theory.semantic.command.node.LabelNode}
 * 展开时不添加命令，只把当前 {@code result.size()} 写入标签。
 * 讲完本类继续看 {@link SemanticResultCallback} 如何把入口段和函数段整理成最终结果。
 *
 * @author Temper
 */
public final class CommandSegmentSupport {
    private CommandSegmentSupport() {
    }

    public static List<SemanticCommand> flatten(CommandNodeRegister register) {
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        // 先把注册器树转换为节点数组，再统一按节点的 flat 规则展开。
        register.register(builder);
        return flatten(builder.build());
    }

    public static List<SemanticCommand> flatten(CommandNode[] nodes) {
        List<SemanticCommand> commands = new ArrayList<>();
        for (CommandNode node : nodes) {
            node.flat(commands);
        }
        return commands;
    }
}
