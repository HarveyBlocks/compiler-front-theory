package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.array.ArrayCreationDimensions;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 翻译数组创建表达式。
 * <p>
 * 该翻译器会先校验元素类型是否合法，再汇总维度声明信息，
 * 最终把各维度长度求值结果与数组元数据一并交给命令工厂。
 */
public class NewArrayTranslator implements CommandTranslator {
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
        SemanticType type = TypeAttributes.childType(context, 1);
        SemanticDiagnostics.requireNotVoid(
                context,
                type,
                TypeAttributes.childAnchor(context, 1),
                "void cannot be used as array element type."
        );
        HeadNode head = context.getTreeContext().peek().toHead();
        ArrayCreationDimensions.Summary summary = ArrayCreationDimensions.summarizeAndValidate(context, head.get(2));
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        // 先求出每个显式维度的长度表达式，再生成数组分配指令。
        children[2].register(builder);
        builder.add(new TerminalNode(context.getCommandFactory().newArray(
                CommandDataType.forStorage(type),
                summary.getTotalDimensions(),
                summary.getSpecifiedDimensions()
        )));
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}
