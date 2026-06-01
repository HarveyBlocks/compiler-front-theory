package org.harvey.vie.theory.semantic.command.register;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;

import java.util.List;

/**
 * 终结命令注册器：包装一条已经生成好的 {@link SemanticCommand}。
 * <p>
 * 常量 token、真实赋值/运算命令、goto 命令等最终都会以 {@link TerminalNode} 的形式写入命令树。
 * 如果这条命令是 {@code break}/{@code continue} 产生的未绑定跳转，本类也会携带对应的
 * {@link UncertainLabelGotoCommand} 列表，供外层循环翻译器绑定。
 * <p>
 * 讲完本类回到 {@link CommandNodeRegister}，下一步看 {@link TerminalNode#flat(java.util.List)}
 * 如何把命令加入线性列表。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:22
 */
public class TokenCommandRegister implements CommandNodeRegister {
    private final SemanticCommand command;
    private final List<UncertainLabelGotoCommand> uncertainBreaks;
    private final List<UncertainLabelGotoCommand> uncertainContinues;

    public TokenCommandRegister(SemanticCommand command) {
        this(command, List.of(), List.of());
    }

    public TokenCommandRegister(
            SemanticCommand command,
            List<UncertainLabelGotoCommand> uncertainBreaks,
            List<UncertainLabelGotoCommand> uncertainContinues) {
        this.command = command;
        this.uncertainBreaks = uncertainBreaks;
        this.uncertainContinues = uncertainContinues;
    }

    @Override
    public void register(CommandNodeBuilder outer) {
        outer.add(new TerminalNode(command));
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainBreaks() {
        return uncertainBreaks;
    }

    @Override
    public List<UncertainLabelGotoCommand> getUncertainContinues() {
        return uncertainContinues;
    }
}
