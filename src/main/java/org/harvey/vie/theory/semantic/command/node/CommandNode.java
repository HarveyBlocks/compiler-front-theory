package org.harvey.vie.theory.semantic.command.node;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.CommandSegmentSupport;
import org.harvey.vie.theory.util.IRandomAccess;

import java.util.List;

/**
 * 讲解主线第 5 站：命令树节点接口。
 * <p>
 * 翻译阶段先构造树形节点，是因为语法规约天然是树形的；最终输出时由
 * {@link CommandSegmentSupport#flatten(org.harvey.vie.theory.semantic.command.register.CommandNodeRegister)}
 * 调用 {@link #flat(List)}，按从左到右的顺序展开成线性的 {@link SemanticCommand} 列表。
 * <p>
 * 主要实现：
 * {@link HeadNode} 递归展开子节点，
 * {@link TerminalNode} 真正追加一条命令，
 * {@link LabelNode} 不追加命令，只记录当前线性位置作为跳转目标。
 * 讲完本接口继续看 {@link HeadNode}、{@link TerminalNode}、{@link LabelNode}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:30
 */
public interface CommandNode extends IRandomAccess<CommandNode> {
    void flat(List<SemanticCommand> result);
}
