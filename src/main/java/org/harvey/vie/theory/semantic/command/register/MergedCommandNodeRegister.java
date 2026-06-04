package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Temper
 */
public class MergedCommandNodeRegister implements CommandNodeRegister {
    private final CommandNodeRegister primary;
    private final List<CommandNodeRegister> extras;
/**
 * 函数功能：创建 MergedCommandNodeRegister 对象。
 * 输入：
 * - primary：CommandNodeRegister 类型参数。
 * - extras：CommandNodeRegister... 类型参数。
 * 输出：无。
 */

    public MergedCommandNodeRegister(CommandNodeRegister primary, CommandNodeRegister... extras) {
        this.primary = primary;
        this.extras = Arrays.asList(extras);
    }
/**
 * 函数功能：注册指定对象。
 * 输入：
 * - outer：CommandNodeBuilder 类型参数。
 * 输出：无。
 */

    @Override
    public void register(CommandNodeBuilder outer) {
        primary.register(outer);
    }
/**
 * 函数功能：获取未确定的 break 跳转列表。
 * 输入：
 * - 无。
 * 输出：List<UncertainLabelGotoCommand> 类型集合或迭代结果。
 */

    @Override
    public List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return merge(primary.getUncertainBreaks(), true);
    }
/**
 * 函数功能：获取未确定的 continue 跳转列表。
 * 输入：
 * - 无。
 * 输出：List<UncertainLabelGotoCommand> 类型集合或迭代结果。
 */

    @Override
    public List<UncertainLabelGotoCommand> getUncertainContinues() {
        return merge(primary.getUncertainContinues(), false);
    }
/**
 * 函数功能：合并命令节点注册器。
 * 输入：
 * - seed：List<UncertainLabelGotoCommand> 类型参数。
 * - breaks：boolean 类型参数。
 * 输出：List<UncertainLabelGotoCommand> 类型集合或迭代结果。
 */

    private List<UncertainLabelGotoCommand> merge(List<UncertainLabelGotoCommand> seed, boolean breaks) {
        List<UncertainLabelGotoCommand> result = new ArrayList<>(seed);
        for (CommandNodeRegister extra : extras) {
            result.addAll(breaks ? extra.getUncertainBreaks() : extra.getUncertainContinues());
        }
        return result;
    }
}

