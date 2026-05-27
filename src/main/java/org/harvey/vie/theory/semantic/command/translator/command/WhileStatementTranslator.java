package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.semantic.error.SemanticDiagnostics;
import org.harvey.vie.theory.semantic.command.command.DefaultSemanticLabel;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.LabelNode;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.PlaceholderNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 把 while 循环翻译为带首部判定的跳转结构。
 * <p>
 * 除了生成循环首尾标签外，还负责把循环体内尚未解析目标的
 * {@code break}/{@code continue} 绑定到当前循环的真实出口。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:35
 */
public class WhileStatementTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 5) {
            throw new org.harvey.vie.theory.exception.CompilerException(
                    "illegal statement on while statement production."
            );
        }
        // while ( expr ) stmt
        // 目标代码结构：
        // whileStart:
        //    expr.command();
        //    ifn_goto whileEnd;
        //    stmt.command();
        //    goto whileStart;
        // whileEnd:
        Boolean constantCondition = ConstantConditionSupport.booleanValue(context, 2);
        if (Boolean.FALSE.equals(constantCondition)) {
            return new PlaceholderNodeRegister();
        }
        SemanticDiagnostics.requireBoolean(
                context,
                TypeAttributes.childType(context, 2),
                TypeAttributes.childAnchor(context, 0),
                "condition must be boolean."
        );
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        SemanticLabel whileStartLabel = new DefaultSemanticLabel();
        SemanticLabel whileEndLabel = new DefaultSemanticLabel();
        thisBuilder.add(new LabelNode(whileStartLabel));
        children[2].register(thisBuilder); // expr
        thisBuilder.add(new TerminalNode(context.getCommandFactory().ifnGoto(whileEndLabel))); // ifn_goto L2
        children[4].register(thisBuilder); // matched_stmt|unmatched_stmt
        thisBuilder.add(new TerminalNode(context.getCommandFactory().gotoCommand(whileStartLabel))); // goto L1
        thisBuilder.add(new LabelNode(whileEndLabel)); // L2
        bindLoopLabels(children[4], whileStartLabel, whileEndLabel);
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }

    /**
     * 将循环体中延迟解析的 break/continue 指令绑定到当前循环。
     *
     * @param body 循环体对应的命令注册器
     * @param continueLabel continue 跳转目标
     * @param breakLabel break 跳转目标
     */
    static void bindLoopLabels(CommandNodeRegister body, SemanticLabel continueLabel, SemanticLabel breakLabel) {
        for (org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand gotoCommand : body.getUncertainBreaks()) {
            if (!gotoCommand.isResolved()) {
                gotoCommand.setLabel(breakLabel);
            }
        }
        for (org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand gotoCommand : body.getUncertainContinues()) {
            if (!gotoCommand.isResolved()) {
                gotoCommand.setLabel(continueLabel);
            }
        }
    }
}
