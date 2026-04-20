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
public class DoWhileStatementTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        // do-while 语句
        //    L1
        //    stmt.command();
        //    expr.command();
        //    CommandFactory.if_goto(L1);
        if (children.length != 2) {
            throw new CompilerException("illegal statement on do while statement production.");
        }
        CommandNodeListBuilder thisBuilder = new CommandNodeListBuilder();
        SemanticLabel whileStartLabel = new DefaultSemanticLabel();
        thisBuilder.add(new LabelNode(whileStartLabel));
        children[0].register(thisBuilder); // stmt
        children[1].register(thisBuilder); // expr
        thisBuilder.add(new TerminalNode(CommandFactory.ifGoto(whileStartLabel))); // if_goto L1
        return new NormalCommandNodeRegister(thisBuilder.toArray(), production);
    }
}
