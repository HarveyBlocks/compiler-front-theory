package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.command.DefaultSemanticLabel;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.node.LabelNode;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
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
        // while 循环语句
        //    L1:
        //    expr.command();
        //    CommandFactory.ifn_goto(L2);
        //    (matched_stmt|unmatched_stmt).command();
        //    CommandFactory.goto(L1);
        //    L2:
        if (children.length != 2) {
            throw new CompilerException("illegal statement on while statement production.");
        }
        CommandNodeListBuilder thisBuilder = new CommandNodeListBuilder();
        SemanticLabel whileStartLabel = new DefaultSemanticLabel();
        SemanticLabel whileEndLabel = new DefaultSemanticLabel();
        thisBuilder.add(new LabelNode(whileStartLabel));
        children[0].register(thisBuilder); // expr
        thisBuilder.add(new TerminalNode(CommandFactory.ifnGoto(whileEndLabel))); // ifn_goto L2
        children[1].register(thisBuilder); // matched_stmt|unmatched_stmt
        thisBuilder.add(new TerminalNode(CommandFactory.gotoCommand(whileStartLabel))); // goto L1
        thisBuilder.add(new LabelNode(whileEndLabel)); // L2
        return new NormalCommandNodeRegister(thisBuilder.toArray(), production);
    }
}
