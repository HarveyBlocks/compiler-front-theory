package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.command.CommandFactory;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.tree.node.HeadNode;
import org.harvey.vie.theory.semantic.tree.node.ShiftReduceSyntaxTreeNode;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.nio.charset.StandardCharsets;

public class FunctionCallTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 4) {
            throw new CompilerException("illegal statement on function call production.");
        }
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        String name = functionName(context);
        children[2].register(builder);
        builder.add(new TerminalNode(CommandFactory.callFunction(name)));
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }

    private String functionName(ShiftReduceSemanticContext context) {
        if (context.getTreeContext().isEmpty() || !context.getTreeContext().peek().isHead()) {
            throw new CompilerException("current reduced head is absent for function call.");
        }
        HeadNode head = context.getTreeContext().peek().toHead();
        ShiftReduceSyntaxTreeNode token = head.get(0);
        if (!token.isToken()) {
            throw new CompilerException("function call name is absent.");
        }
        return new String(token.toToken().getSource().getLexeme(), StandardCharsets.UTF_8);
    }
}
