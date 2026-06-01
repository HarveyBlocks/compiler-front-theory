package org.harvey.vie.theory.semantic.command.node;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.CommandSegmentSupport;
import org.harvey.vie.theory.util.IRandomAccess;

import java.util.List;

/**
 * 讲解主线第 5 站：命令树节点接口。
 * <p>
 * 自底向上的规约过程天然会形成一棵树：表达式、语句、块、程序顶层都先是树形结构。
 * 因此本项目先把中间代码组织为命令节点树，再由
 * {@link CommandSegmentSupport#flatten(org.harvey.vie.theory.semantic.command.register.CommandNodeRegister)}
 * 调用 {@link #flat(List)} 按从左到右的顺序线性化。这一步对应编译原理里把语法树/中间表示展开为
 * 顺序三地址码序列。
 * <p>
 * 主要实现：
 * {@link HeadNode} 对应非终结符规约结果，负责递归展开子节点；
 * {@link TerminalNode} 对应真正的一条 {@link SemanticCommand}；
 * {@link LabelNode} 不产生文本命令，只记录当前线性位置，供跳转命令回填目标地址。
 * <p>
 * 主线下一站：{@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory}。
 * 下一站会讲 {@link TerminalNode} 里保存的 {@link SemanticCommand} 到底由谁创建、命令文本从哪里来。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:30
 */
public interface CommandNode extends IRandomAccess<CommandNode> {
    void flat(List<SemanticCommand> result);
}
