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
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.type.TypeAttributes;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * 控制流支路：把 {@code do-while} 翻译为“先执行一次，再检测条件”的跳转结构。
 * <p>
 * 与 {@link WhileStatementTranslator} 不同，循环体至少会执行一次，因此在条件恒为 {@code false} 时
 * 也仍然需要保留一次循环体代码。普通场景下的结构是：循环体起点标签、循环体、条件检测点标签、
 * 条件求值、{@code if_goto whileStart}、循环结束标签。
 * <p>
 * {@code continue} 在 do-while 中不能跳回循环体开头，而要跳到条件检测点，所以本类调用
 * {@link WhileStatementTranslator#bindLoopLabels(CommandNodeRegister, SemanticLabel, SemanticLabel)}
 * 时把 {@code continueLabel} 设为 {@code beforeTestLabel}。讲完本支路回到
 * {@link org.harvey.vie.theory.semantic.tag.TagStrategyCompose}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:35
 */
public class DoWhileStatementTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 7) {
            throw new org.harvey.vie.theory.exception.CompilerException(
                    "illegal statement on do while statement production."
            );
        }
        Boolean constantCondition = ConstantConditionSupport.booleanValue(context, 4);
        if (Boolean.FALSE.equals(constantCondition)) {
            return onFalseConstantCondition(production, children);
        }
        return onNormalCondition(context, production, children);
    }

    private static NormalCommandNodeRegister onNormalCondition(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        SemanticDiagnostics.requireBoolean(
                context,
                TypeAttributes.childType(context, 4),
                TypeAttributes.childAnchor(context, 2),
                "do-while condition must be boolean."
        );
        // do stmt while ( expr ) ;
        // 目标代码结构：
        // whileStart:
        //    stmt.command();
        // beforeTest:
        //    expr.command();
        //    if_goto whileStart;
        // whileEnd:
        SemanticLabel whileStartLabel = new DefaultSemanticLabel();
        SemanticLabel beforeTestLabel = new DefaultSemanticLabel();
        SemanticLabel whileEndLabel = new DefaultSemanticLabel();
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        thisBuilder.add(new LabelNode(whileStartLabel));
        children[1].register(thisBuilder); // stmt
        thisBuilder.add(new LabelNode(beforeTestLabel));
        children[4].register(thisBuilder); // expr
        thisBuilder.add(new TerminalNode(context.getCommandFactory().ifGoto(whileStartLabel)));
        thisBuilder.add(new LabelNode(whileEndLabel));
        WhileStatementTranslator.bindLoopLabels(children[1], beforeTestLabel, whileEndLabel);
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }

    private static NormalCommandNodeRegister onFalseConstantCondition(
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        // 条件恒假时仍需执行一次循环体，随后直接落到循环末尾。
        SemanticLabel whileStartLabel = new DefaultSemanticLabel();
        SemanticLabel whileEndLabel = new DefaultSemanticLabel();
        CommandNodeBuilder thisBuilder = new CommandNodeListBuilder();
        thisBuilder.add(new LabelNode(whileStartLabel));
        children[1].register(thisBuilder);
        thisBuilder.add(new LabelNode(whileEndLabel));
        WhileStatementTranslator.bindLoopLabels(children[1], whileEndLabel, whileEndLabel);
        return new NormalCommandNodeRegister(thisBuilder.build(), production, children);
    }
}
