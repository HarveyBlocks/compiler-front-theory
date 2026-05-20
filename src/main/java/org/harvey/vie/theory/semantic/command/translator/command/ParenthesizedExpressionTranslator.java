package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

public class ParenthesizedExpressionTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 3) {
            throw new CompilerException("illegal statement on parenthesized expression production.");
        }
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        children[1].register(builder);
        return new NormalCommandNodeRegister(
                builder.build(),
                production,
                children,
                children[1].getType(),
                children[1].getInstructionType(),
                children[1].getAnchorToken()
        );
    }
}
