package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;

import java.util.List;

/**
 * TODO
 *  Register 本来是用来处理label 与 outer 的关系的. <br>
 *  label如何注册, 可能要看 outer 的具体情况 <br>
 *
 * @author Temper
 */
public interface CommandNodeRegister {
    /**
     * 函数功能：注册指定对象。
     * 输入：
     * - outer：CommandNodeBuilder 类型参数。
     * 输出：无。
     */
    void register(CommandNodeBuilder outer);

    /**
     * 函数功能：获取未确定的 break 跳转列表。
     * 输入：
     * - 无。
     * 输出：List<UncertainLabelGotoCommand> 类型集合或迭代结果。
     */
    default List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return List.of();
    }

    /**
     * 函数功能：获取未确定的 continue 跳转列表。
     * 输入：
     * - 无。
     * 输出：List<UncertainLabelGotoCommand> 类型集合或迭代结果。
     */
    default List<UncertainLabelGotoCommand> getUncertainContinues() {
        return List.of();
    }
}

