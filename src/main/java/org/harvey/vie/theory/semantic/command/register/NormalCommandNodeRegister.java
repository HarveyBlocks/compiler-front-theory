package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.HeadNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:13
 */
public class NormalCommandNodeRegister implements CommandNodeRegister {
    private final CommandNode[] childrenNode;
    private final SimpleGrammarProduction production;
    private final List<UncertainLabelGotoCommand> uncertainBreaks;
    private final List<UncertainLabelGotoCommand> uncertainContinues;

    /**
     * 函数功能：创建 NormalCommandNodeRegister 对象。
     * 输入：
     * - childrenNode：CommandNode[] 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * - uncertainBreaks：List<UncertainLabelGotoCommand> 类型参数。
     * - uncertainContinues：List<UncertainLabelGotoCommand> 类型参数。
     * 输出：无。
     */

    public NormalCommandNodeRegister(
            CommandNode[] childrenNode,
            SimpleGrammarProduction production,
            List<UncertainLabelGotoCommand> uncertainBreaks,
            List<UncertainLabelGotoCommand> uncertainContinues) {
        this.childrenNode = childrenNode;
        this.production = production;
        this.uncertainBreaks = uncertainBreaks;
        this.uncertainContinues = uncertainContinues;
    }

    /**
     * 函数功能：创建 NormalCommandNodeRegister 对象。
     * 输入：
     * - childrenNode：CommandNode[] 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */

    public NormalCommandNodeRegister(CommandNode[] childrenNode, SimpleGrammarProduction production) {
        this(childrenNode, production, List.of(), List.of());
    }

    /**
     * 函数功能：创建 NormalCommandNodeRegister 对象。
     * 输入：
     * - childrenNode：CommandNode[] 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * - childrenRegisters：CommandNodeRegister[] 类型参数。
     * 输出：无。
     */

    public NormalCommandNodeRegister(
            CommandNode[] childrenNode,
            SimpleGrammarProduction production,
            CommandNodeRegister[] childrenRegisters) {
        this(
                childrenNode,
                production,
                collectBreaks(childrenRegisters),
                collectContinues(childrenRegisters)
        );
    }

    /**
     * 函数功能：收集未解析的 break 跳转。
     * 输入：
     * - childrenRegisters：CommandNodeRegister[] 类型参数。
     * 输出：List<UncertainLabelGotoCommand> 类型集合或迭代结果。
     */

    private static List<UncertainLabelGotoCommand> collectBreaks(CommandNodeRegister[] childrenRegisters) {
        List<UncertainLabelGotoCommand> result = new ArrayList<>();
        for (CommandNodeRegister child : childrenRegisters) {
            result.addAll(child.getUncertainBreaks());
        }
        return result;
    }

    /**
     * 函数功能：收集未解析的 continue 跳转。
     * 输入：
     * - childrenRegisters：CommandNodeRegister[] 类型参数。
     * 输出：List<UncertainLabelGotoCommand> 类型集合或迭代结果。
     */

    private static List<UncertainLabelGotoCommand> collectContinues(CommandNodeRegister[] childrenRegisters) {
        List<UncertainLabelGotoCommand> result = new ArrayList<>();
        for (CommandNodeRegister child : childrenRegisters) {
            result.addAll(child.getUncertainContinues());
        }
        return result;
    }

    /**
     * 函数功能：注册指定对象。
     * 输入：
     * - outer：CommandNodeBuilder 类型参数。
     * 输出：无。
     */

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new HeadNode(childrenNode, production));
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
