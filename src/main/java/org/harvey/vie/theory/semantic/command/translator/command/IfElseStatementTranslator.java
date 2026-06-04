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
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 把 if-else 语句翻译为三地址形式的条件跳转与顺序标签。
 * <p>
 * 当条件已经在编译期折叠为常量时，会直接裁剪掉不可达分支，
 * 从而避免生成多余的跳转指令。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:35
 */
public class IfElseStatementTranslator implements CommandTranslator {
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
        if (children.length != 7) {
            throw new org.harvey.vie.theory.exception.CompilerException(
                    "illegal statement on if-else statement production."
            );
        }
        // if ( expr ) stmt else stmt
        // 目标代码结构：
        //    expr.command();
        //    ifn_goto elseStart;
        //    then.command();
        //    goto elseEnd;
        // elseStart:
        //    else.command();
        // elseEnd:
        Boolean constantCondition = ConstantConditionSupport.booleanValue(context, 2);
        if (constantCondition != null) {
            return constantCondition
                    ? new MergedCommandNodeRegister(children[4], children[6])
                    : new MergedCommandNodeRegister(children[6], children[4]);
        }
        SemanticDiagnostics.requireBoolean(
                context,
                TypeAttributes.childType(context, 2),
                TypeAttributes.childAnchor(context, 0),
                "condition must be boolean."
        );
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        SemanticLabel elseStartLabel = new DefaultSemanticLabel();
        SemanticLabel elseEndLabel = new DefaultSemanticLabel();
        // 先计算条件，再按真假分流到 then/else 两个代码块。
        children[2].register(thisBuilder);
        thisBuilder.add(new TerminalNode(context.getCommandFactory().ifnGoto(elseStartLabel)));
        children[4].register(thisBuilder);
        thisBuilder.add(new TerminalNode(context.getCommandFactory().gotoCommand(elseEndLabel)));
        thisBuilder.add(new LabelNode(elseStartLabel));
        children[6].register(thisBuilder);
        thisBuilder.add(new LabelNode(elseEndLabel));
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}
