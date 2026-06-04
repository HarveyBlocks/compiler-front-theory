package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 翻译函数调用表达式。
 * <p>
 * 参数求值代码会先按既定顺序入栈，随后再发出调用指令，
 * 以保证被调函数读取到的实参与语义分析阶段的顺序一致。
 *
 * @author Temper
 */
public class FunctionCallTranslator implements CommandTranslator {
    /**
     * 函数功能：翻译语法节点并返回命令节点注册器。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * - children：CommandNodeRegister[] 类型参数。
     * 输出：CommandNodeRegister 类型返回值。
     */
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        FunctionRecord record = function(context);
        children[2].register(builder);
        builder.add(new TerminalNode(context.getCommandFactory().callFunction(record)));
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }

    /**
     * 函数功能：获取函数记录。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * 输出：FunctionRecord 类型返回值。
     */
    private FunctionRecord function(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new CompilerException("current reduced head is absent for function call.");
        }
        HeadNode head = context.getTreeContext().peek().toHead();
        ShiftReduceSyntaxTreeNode tokenNode = head.get(0);
        if (!tokenNode.isToken()) {
            throw new CompilerException("function call name is absent.");
        }
        FunctionRecord record = context.getFunction(tokenNode.toToken().getSource());
        if (record == null) {
            throw new CompilerException("function is not declared in current visible scope.");
        }
        return record;
    }
}

