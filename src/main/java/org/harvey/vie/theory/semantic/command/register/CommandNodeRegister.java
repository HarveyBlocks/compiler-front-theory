package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;

import java.util.List;

/**
 * TODO 现在看来似乎不需要这个机制了. <br>
 *  Register 本来是用来处理label 与 outer 的关系的. <br>
 *  label如何注册, 可能要看 outer 的具体情况 <br>
 */
public interface CommandNodeRegister {
    void register(CommandNodeBuilder outer);

    default List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return List.of();
    }

    default List<UncertainLabelGotoCommand> getUncertainContinues() {
        return List.of();
    }
}
