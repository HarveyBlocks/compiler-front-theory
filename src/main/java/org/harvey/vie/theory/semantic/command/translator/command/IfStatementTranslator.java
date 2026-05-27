package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.command.command.DefaultSemanticLabel;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.LabelNode;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.MergedCommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 把不带 else 的 if 语句翻译为条件跳转。
 * <p>
 * 如果条件已被求值为编译期常量，则直接保留可达分支，
 * 不再生成无意义的跳转与空标签。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:35
 */
public class IfStatementTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 5) {
            throw new org.harvey.vie.theory.exception.CompilerException(
                    "illegal statement on if statement production."
            );
        }
        // if ( expr ) stmt
        // 目标代码结构：
        //    expr.command();
        //    ifn_goto ifEnd;
        //    stmt.command();
        // ifEnd:
        Boolean constantCondition = ConstantConditionSupport.booleanValue(context, 2);
        if (constantCondition != null) {
            return constantCondition
                    ? new MergedCommandNodeRegister(children[4])
                    : new MergedCommandNodeRegister(new PlaceholderNodeRegister(), children[4]);
        }
        SemanticDiagnostics.requireBoolean(
                context,
                TypeAttributes.childType(context, 2),
                TypeAttributes.childAnchor(context, 0),
                "condition must be boolean."
        );
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        SemanticLabel ifEndLabel = new DefaultSemanticLabel();
        // 条件为假时直接跳过语句体。
        children[2].register(thisBuilder);
        thisBuilder.add(new TerminalNode(context.getCommandFactory().ifnGoto(ifEndLabel)));
        children[4].register(thisBuilder);
        thisBuilder.add(new LabelNode(ifEndLabel));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}
