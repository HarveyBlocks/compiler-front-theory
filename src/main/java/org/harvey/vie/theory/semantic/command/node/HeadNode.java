package org.harvey.vie.theory.semantic.command.node;

import lombok.Getter;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.util.IRandomAccess;

import java.util.List;

/**
 * 非终结命令节点：对应一次语法规约后的产生式头。
 * <p>
 * 它保存若干子 {@link CommandNode} 和当前 {@link SimpleGrammarProduction}。展开时不额外生成命令，
 * 只按子节点顺序递归调用 {@link CommandNode#flat(List)}。因此命令顺序由翻译器构建子节点数组的顺序决定。
 * <p>
 * 讲完本类可回到 {@link CommandNode}，或看真正写入命令的 {@link TerminalNode}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 01:29
 */
@Getter
public class HeadNode extends IRandomAccess.ArrayImpl<CommandNode> implements
        CommandNode {
    private final SimpleGrammarProduction production;

    public HeadNode(CommandNode[] children, SimpleGrammarProduction production) {
        super(children);
        this.production = production;
    }

    @Override
    public void flat(List<SemanticCommand> result) {
        // 递归展开子节点；HeadNode 自己不占用命令下标。
        for (CommandNode child : this) {
            child.flat(result);
        }
    }
}
