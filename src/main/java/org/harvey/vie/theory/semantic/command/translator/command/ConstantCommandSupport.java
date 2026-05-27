package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.command.factory.DefaultCommandFactory;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.value.ConstantValue;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 把已知常量表达式直接转成装载常量的命令节点。
 *
 * @author Temper
 */
final class ConstantCommandSupport {
    private ConstantCommandSupport() {
    }

    /**
     * 如果当前归约节点已经有常量值，就返回只包含常量装载指令的注册器；
     * 否则返回 null，让调用方继续走常规翻译流程。
     */
    static CommandNodeRegister constantOrNull(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        HeadNode head = currentReducedHead(context);
        ConstantValue constantValue = context.getConstantValue(head);
        if (constantValue == null) {
            return null;
        }
        CommandNode node = new TerminalNode(context.getCommandFactory().loadConstant(constantValue));
        return new NormalCommandNodeRegister(new CommandNode[]{node}, production, children);
    }

    /**
     * 获取当前正在归约的头节点，以便读取该表达式的常量传播结果。
     */
    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }
}

