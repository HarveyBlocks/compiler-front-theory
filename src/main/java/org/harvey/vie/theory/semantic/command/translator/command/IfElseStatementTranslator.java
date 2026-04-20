
package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.CommandContext;
import org.harvey.vie.theory.semantic.command.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 00:35
 */
public class IfElseStatementTranslator implements CommandTranslator {
    @Override
    public CommandContext.CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandContext.CommandNodeRegister[] children) {
        // if-else 语句
        //    expr.command();
        //    CommandFactory.ifn_goto(L1);
        //    stmt.command();
        //    CommandFactory.ifn_goto(L2);
        //    L1:
        //    (unmatched_stmt|matched_stmt).command();
        //    L2:
        if (children.length != 3) {
            throw new CompilerException("illegal statement on if-else statement production.");
        }
        CommandNodeListBuilder thisBuilder = new CommandNodeListBuilder();
        CommandContext.Label elseStartLabel = new CommandContext.DefaultLabel();
        CommandContext.Label elseEndLabel = new CommandContext.DefaultLabel();
        children[0].register(thisBuilder); // expr
        thisBuilder.add(new CommandContext.TerminalNode(CommandFactory.ifnGoto(elseStartLabel))); // ifn_goto L1
        children[1].register(thisBuilder); // stmt
        thisBuilder.add(new CommandContext.LabelNode(elseStartLabel));
        children[2].register(thisBuilder); // (unmatched_stmt|matched_stmt)
        thisBuilder.add(new CommandContext.LabelNode(elseEndLabel));
        return new NormalCommandNodeRegister(thisBuilder.toArray(), production);
    }
}
