package org.harvey.vie.theory.semantic.command.register;

import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 合并注册器：注册时只输出主分支命令，但未绑定跳转信息会同时合并多个分支。
 * <p>
 * 这个类主要服务于常量条件优化。例如 {@code if (true) then else other} 只应输出 then 分支命令；
 * 但为了保证死分支里的非法 {@code break}/{@code continue} 仍能被顶层诊断，extras 分支里的
 * {@link UncertainLabelGotoCommand} 也会被收集出来。
 * <p>
 * 讲完本类回到 {@link CommandNodeRegister}，或看控制流优化入口
 * {@link org.harvey.vie.theory.semantic.command.translator.command.IfElseStatementTranslator}。
 *
 * @author Temper
 */
public class MergedCommandNodeRegister implements CommandNodeRegister {
    private final CommandNodeRegister primary;
    private final List<CommandNodeRegister> extras;

    public MergedCommandNodeRegister(CommandNodeRegister primary, CommandNodeRegister... extras) {
        this.primary = primary;
        this.extras = Arrays.asList(extras);
    }

    @Override
    public void register(CommandNodeBuilder outer) {
        primary.register(outer);
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return merge(primary.getUncertainBreaks(), true);
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainContinues() {
        return merge(primary.getUncertainContinues(), false);
    }

    private List<UncertainLabelGotoCommand> merge(List<UncertainLabelGotoCommand> seed, boolean breaks) {
        List<UncertainLabelGotoCommand> result = new ArrayList<>(seed);
        for (CommandNodeRegister extra : extras) {
            result.addAll(breaks ? extra.getUncertainBreaks() : extra.getUncertainContinues());
        }
        return result;
    }
}
