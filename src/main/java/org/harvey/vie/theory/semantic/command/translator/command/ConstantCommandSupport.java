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
    /**
     * 函数功能：创建 ConstantCommandSupport 对象。
     * 输入：
     * - 无。
     * 输出：无。
     */
    private ConstantCommandSupport() {
    }

    /**
     * 函数功能：获取常量命令节点或空结果。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * - children：CommandNodeRegister[] 类型参数。
     * 输出：CommandNodeRegister 类型返回值。
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
     * 函数功能：获取当前规约头节点。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：HeadNode 类型返回值。
     */
    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new IllegalStateException("current reduced head is not available");
        }
        return context.getTreeContext().peek().toHead();
    }
}

