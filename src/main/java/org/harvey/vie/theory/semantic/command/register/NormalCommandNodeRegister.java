package org.harvey.vie.theory.semantic.command.register;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.HeadNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通非终结注册器：保存一次规约后形成的 {@link HeadNode}。
 * <p>
 * 翻译器把已经拼好的子 {@link CommandNode} 数组交给它；外层调用
 * {@link #register(CommandNodeBuilder)} 时，它会把这些子节点包装成一个 {@link HeadNode} 写入外层。
 * 同时它会从所有子 {@link CommandNodeRegister} 收集未绑定的 {@code break}/{@code continue}，
 * 继续向外层冒泡，直到被循环支路绑定或被 {@link org.harvey.vie.theory.semantic.command.translator.command.ProgramCommandTranslator}
 * 判定为非法。
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

    public NormalCommandNodeRegister(CommandNode[] childrenNode, SimpleGrammarProduction production) {
        this(childrenNode, production, List.of(), List.of());
    }

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

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new HeadNode(childrenNode, production));
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return uncertainBreaks;
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainContinues() {
        return uncertainContinues;
    }

    private static List<UncertainLabelGotoCommand> collectBreaks(CommandNodeRegister[] childrenRegisters) {
        List<UncertainLabelGotoCommand> result = new ArrayList<>();
        for (CommandNodeRegister child : childrenRegisters) {
            result.addAll(child.getUncertainBreaks());
        }
        return result;
    }

    private static List<UncertainLabelGotoCommand> collectContinues(CommandNodeRegister[] childrenRegisters) {
        List<UncertainLabelGotoCommand> result = new ArrayList<>();
        for (CommandNodeRegister child : childrenRegisters) {
            result.addAll(child.getUncertainContinues());
        }
        return result;
    }
}
