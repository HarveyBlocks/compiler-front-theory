package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * Translates struct instantiation expressions.
 *
 * @author Temper
 */
public class NewStructTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        HeadNode head = context.getTreeContext().peek().toHead();
        SourceToken nameToken = head.get(1).toToken().getSource();
        CommandNode node = new TerminalNode(context.getCommandFactory().newStruct(nameToken));
        return new NormalCommandNodeRegister(new CommandNode[]{node}, production, children);
    }
}
