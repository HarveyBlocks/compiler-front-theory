package org.harvey.vie.theory.semantic.command.node;

import lombok.Getter;
import lombok.Setter;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.util.IRandomAccess;

import java.util.List;

/**
 * 标签节点：不生成命令文本，只在展开时把当前位置写入 {@link SemanticLabel}。
 * <p>
 * 控制流翻译器先把 {@link LabelNode} 放进命令树，再把同一个 {@link SemanticLabel} 交给
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#ifGoto(SemanticLabel)}、
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#ifnGoto(SemanticLabel)} 或
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#gotoCommand(SemanticLabel)}。
 * 等 {@link org.harvey.vie.theory.semantic.command.CommandSegmentSupport} 展开到这里时，标签的
 * index 就是“下一条真实命令”的下标，跳转命令打印时会读取这个下标。
 * <p>
 * 讲完本类回到 {@link CommandNode}，然后继续看命令工厂
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:29
 */
@Getter
@Setter
public class LabelNode extends IRandomAccess.EmptyImpl<CommandNode> implements
        CommandNode {
    private final SemanticLabel label;

    public LabelNode(SemanticLabel label) {this.label = label;}

    @Override
    public void flat(List<SemanticCommand> result) {
        label.setIndex(result.size());
    }
}
