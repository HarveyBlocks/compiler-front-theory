package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * Materializes identifier use into a bound local-address command after symbol resolution.
 *
 * @author Temper
 */
public class IdentifierUseTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        HeadNode head = currentReducedHead(context);
        ShiftReduceSyntaxTreeNode child = head.get(0);
        if (!child.isToken()) {
            throw new CompilerException("identifier use requires a token child.");
        }
        SourceToken token = child.toToken().getSource();
        IdentifierRecord record = context.getIdentifier(token);
        if (record == null) {
            throw new CompilerException("identifier is not declared in current visible scopes.");
        }
        CommandNode node = new TerminalNode(context.getCommandFactory().loadIdentifierAddress(record));
        return new NormalCommandNodeRegister(new CommandNode[]{node}, production, children);
    }

    private static HeadNode currentReducedHead(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new CompilerException("current reduced head is absent for identifier use.");
        }
        return context.getTreeContext().peek().toHead();
    }
}
