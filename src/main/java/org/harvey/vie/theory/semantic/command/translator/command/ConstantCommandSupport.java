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
 * 表达式支路的常量折叠辅助类：把已知常量表达式直接转成装载常量的命令节点。
 * <p>
 * 类型和常量传播由前面的语义回调维护在 {@link ShiftReduceSemanticContext#getConstantValue} 中；
 * 命令生成阶段只负责复用这个结果。
 * 如果能静态确定值，就用
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#loadConstant(ConstantValue)}
 * 生成一条 {@code load_st_*_static}，从而省掉原本的子表达式求值命令。
 * <p>
 * 本类是支路工具，讲完回到调用方 {@link InSuffixExpressionTranslator}、
 * {@link UnaryExpressionTranslator} 或 {@link ParenthesizedExpressionTranslator}。
 *
 * @author Temper
 */
final class ConstantCommandSupport {
    private ConstantCommandSupport() {
    }

    /**
     * 如果当前归约节点已经有常量值，就返回只包含常量装载指令的注册器；
     * 否则返回 null，让调用方继续走常规翻译流程。
     * <p>
     * 注意：这里仍返回 {@link CommandNodeRegister}，这样常量优化后的表达式可以像普通表达式一样被外层组合。
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
