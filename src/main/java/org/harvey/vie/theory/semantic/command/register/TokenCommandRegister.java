package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:22
 */
public class TokenCommandRegister implements CommandNodeRegister {
    private final SemanticCommand command;
    private final List<UncertainLabelGotoCommand> uncertainBreaks;
    private final List<UncertainLabelGotoCommand> uncertainContinues;

    /**
     * 函数功能：创建 TokenCommandRegister 对象。
     * 输入：
     * - command：SemanticCommand 类型参数。
     * 输出：无。
     */

    public TokenCommandRegister(SemanticCommand command) {
        this(command, List.of(), List.of());
    }

    /**
     * 函数功能：创建 TokenCommandRegister 对象。
     * 输入：
     * - command：SemanticCommand 类型参数。
     * - uncertainBreaks：List<UncertainLabelGotoCommand> 类型参数。
     * - uncertainContinues：List<UncertainLabelGotoCommand> 类型参数。
     * 输出：无。
     */

    public TokenCommandRegister(
            SemanticCommand command,
            List<UncertainLabelGotoCommand> uncertainBreaks,
            List<UncertainLabelGotoCommand> uncertainContinues) {
        this.command = command;
        this.uncertainBreaks = uncertainBreaks;
        this.uncertainContinues = uncertainContinues;
    }

    /**
     * 函数功能：注册指定对象。
     * 输入：
     * - outer：CommandNodeBuilder 类型参数。
     * 输出：无。
     */

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new TerminalNode(command));
    }

    /**
     * 函数功能：获取未确定的 break 跳转列表。
     * 输入：
     * - 无。
     * 输出：List<UncertainLabelGotoCommand> 类型集合或迭代结果。
     */

    @Override
    public List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return uncertainBreaks;
    }

    /**
     * 函数功能：获取未确定的 continue 跳转列表。
     * 输入：
     * - 无。
     * 输出：List<UncertainLabelGotoCommand> 类型集合或迭代结果。
     */

    @Override
    public List<UncertainLabelGotoCommand> getUncertainContinues() {
        return uncertainContinues;
    }
}
