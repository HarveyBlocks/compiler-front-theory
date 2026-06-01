package org.harvey.vie.theory.semantic.command.node;

import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;

import java.util.Stack;

/**
 * 命令生成专用的移进-规约辅助栈。
 * <p>
 * {@link org.harvey.vie.theory.semantic.command.CommandBuildCallback} 每移进一个 token 就压入一个
 * {@link CommandNodeRegister}，每规约一个产生式就弹出右部数量的注册器并压回规约结果。
 * 它和语法分析状态栈同步推进，但只保存命令生成需要的语义对象。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 16:25
 */
public class CommandContext extends Stack<CommandNodeRegister> {


}
