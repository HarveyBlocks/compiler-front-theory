package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.command.LocationKind;
import org.harvey.vie.theory.semantic.command.command.factory.CommandDataType;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:26
 */
public class PrimaryProduceLeftValueTranslator implements CommandTranslator {
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
        CommandNodeRegister constant = ConstantCommandSupport.constantOrNull(context, production, children);
        if (constant != null) {
            return constant;
        }
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        children[0].register(builder);
        var register = TypeAttributes.childHasType(context, 0)
                ? TypeAttributes.child(context, 0)
                : TypeAttributes.result(context);
        LocationKind locationKind = register.getLocationKind();
        if (locationKind == LocationKind.REFERENCE) {
            builder.add(new TerminalNode(context.getCommandFactory().stTopRefToVal(
                    CommandDataType.forStorage(register.requireType("semantic type is required for left value."))
            )));
        } else {
            builder.add(new TerminalNode(context.getCommandFactory().stTopAddrToVal(
                    CommandDataType.forStorage(register.requireType("semantic type is required for left value."))
            )));
        }
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}
