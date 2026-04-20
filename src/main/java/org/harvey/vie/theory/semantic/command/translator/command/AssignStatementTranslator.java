package org.harvey.vie.theory.semantic.command.translator.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * TODO assignment_stmt->lvalue = expr
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
            SimpleGrammarProduction production, CommandNodeRegister[] children) {
        // lvalue是reference, expr是value, 直接赋值即可
        // lvalue.command();
        // expr.command();
        // CommandFactory.assign_from_st_top_to_ref();
        if (children.length != 2) {
            throw new CompilerException("illegal statement on assign statement production.");
        }
        CommandNodeListBuilder thisBuilder = new CommandNodeListBuilder();
        children[0].register(thisBuilder); // lvalue
        children[1].register(thisBuilder); // expr
        thisBuilder.add(new TerminalNode(CommandFactory.assignFromStTopToRef()));
        return new NormalCommandNodeRegister(thisBuilder.toArray(), production);
    }
}
