package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.semantic.command.LocationKind;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.type.SemanticType;
import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 赋值支路：处理 {@code loc = bool;} 这类普通赋值语句。
 * <p>
 * 本翻译器是讲“三地址码风格命令”的关键例子：左值子树先生成“目标位置”，右值子树再生成“栈顶值”，
 * 最后根据 {@link LocationKind} 选择写回命令。普通变量是 {@link LocationKind#ADDRESS}，写回走
 * {@code assign_from_st_top_to_addr_*}；数组元素、结构体字段等间接位置是 {@link LocationKind#REFERENCE}，
 * 写回走 {@code assign_from_st_top_to_ref_*}。
 * <p>
 * 如果右值类型可以隐式转成左值类型，先通过
 * {@link org.harvey.vie.theory.semantic.command.command.factory.CommandFactory#stTopCast(CommandDataType, CommandDataType)}
 * 插入栈顶类型转换。讲完赋值支路可继续看表达式支路 {@link InSuffixExpressionTranslator}，或回到
 * {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class AssignStatementTranslator implements CommandTranslator {

    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        SemanticType targetType = TypeAttributes.childType(context, 0);
        SemanticType sourceType = TypeAttributes.childType(context, 2);
        SemanticDiagnostics.requireAssignable(
                context,
                sourceType,
                targetType,
                TypeAttributes.childAnchor(context, 1),
                "assignment requires assignable types."
        );
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        // 先求左值定位信息，再求右值，保证最终写回时栈顶保存的是待赋值结果。
        children[0].register(builder);
        children[2].register(builder);
        if (context.requiresImplicitCast(sourceType, targetType)) {
            // 赋值兼容但底层存储类型不同的场景，需要在写回前补一次栈顶转换。
            builder.add(new TerminalNode(context.getCommandFactory().stTopCast(
                    CommandDataType.forValue(sourceType),
                    CommandDataType.forValue(targetType)
            )));
        }
        LocationKind locationKind = TypeAttributes.child(context, 0).getLocationKind();
        if (locationKind == LocationKind.REFERENCE) {
            // 引用左值保存的是“指向目标存储单元的引用”，因此按引用写回。
            builder.add(new TerminalNode(context.getCommandFactory().assignFromStTopToRef(
                    CommandDataType.forStorage(targetType)
            )));
        } else {
            // 普通左值直接持有目标地址，按地址写回即可。
            builder.add(new TerminalNode(context.getCommandFactory().assignFromStTopToAddr(
                    CommandDataType.forStorage(targetType)
            )));
        }
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}
