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
 * 创建对象支路：翻译数组创建表达式 {@code new T[d1][d2]...}。
 * <p>
 * 该翻译器先校验元素类型，再通过 {@link ArrayCreationDimensions#summarizeAndValidate} 汇总总维度数、
 * 显式给出长度的维度数。命令顺序是：先注册各维度长度表达式，再追加
 * {@code new_array_<elementType> total specified}。
 * <p>
 * 例如三维 int32 数组、只给前两维长度，会输出 {@code new_array_int32 3 2}。
 * 讲完本支路可看 {@link NewStructTranslator}，或回到
 * {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
 */
public class NewArrayTranslator implements CommandTranslator {
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
