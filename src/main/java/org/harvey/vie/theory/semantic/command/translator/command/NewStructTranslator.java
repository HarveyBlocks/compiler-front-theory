package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 翻译结构体实例化表达式。
 * <p>
 * 这里会根据语法树中的结构体名查找结构体定义，
 * 并生成一条按记录布局分配对象的创建指令。
 *
 * @author Temper
 */
public class NewStructTranslator implements CommandTranslator {
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
        HeadNode head = context.getTreeContext().peek().toHead();
        SourceToken nameToken = head.get(1).toToken().getSource();
        StructRecord record = context.getStruct(nameToken);
        if (record == null) {
            SemanticDiagnostics.reject(context, nameToken, "struct type is not declared.");
        }
        // 结构体实例的内存布局完全由 StructRecord 描述。
        CommandNode node = new TerminalNode(context.getCommandFactory().newStruct(record));
        return new NormalCommandNodeRegister(new CommandNode[]{node}, production, children);
    }
}
