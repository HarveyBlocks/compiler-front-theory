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
 * 翻译赋值语句，负责完成类型兼容性检查、必要的隐式类型转换，
 * 以及按左值位置类型选择不同的写回指令。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 08:29
 */
@AllArgsConstructor
public class AssignStatementTranslator implements CommandTranslator {
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
