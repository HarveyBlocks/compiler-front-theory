package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * 讲解主线第 7 站：把命令树线性化为中间代码序列。
 * <p>
 * 编译原理里的三地址码通常是一条一条顺序执行的中间代码，而前面几站为了配合语法规约，先保存成树。
 * 本工具完成“树到线性序列”的转换：先让 {@link CommandNodeRegister} 把节点写入
 * {@link CommandNodeListBuilder}，再从左到右调用 {@link CommandNode#flat(List)}。
 * 展开结果就是后续保存和打印的 {@link SemanticCommand} 列表。
 * <p>
 * 标签解析也发生在这里：{@link org.harvey.vie.theory.semantic.command.node.LabelNode}
 * 展开时不添加命令，只把当前 {@code result.size()} 写入标签。这样 {@code if_goto}/{@code goto}
 * 打印时就能拿到确定的目标行号。
 * <p>
 * 主线下一站：{@link SemanticResultCallback}。下一站会讲线性化后的入口段和函数段如何封装成语义分析结果。
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
