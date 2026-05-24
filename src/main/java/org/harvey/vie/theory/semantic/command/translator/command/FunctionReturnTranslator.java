package org.harvey.vie.theory.semantic.command.translator.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.command.command.factory.DefaultCommandFactory;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.node.TerminalNode;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.register.NormalCommandNodeRegister;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.semantic.value.ConstantAttributes;
import org.harvey.vie.theory.semantic.value.ConstantValue;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

/**
 * @author Temper
 */
public class FunctionReturnTranslator implements CommandTranslator {
    @Override
    public CommandNodeRegister translate(
            ShiftReduceSemanticContext context,
            SimpleGrammarProduction production,
            CommandNodeRegister[] children) {
        if (children.length != 2 && children.length != 3) {
            throw new CompilerException("illegal statement on return statement production.");
        }
        CommandNodeBuilder builder = new CommandNodeListBuilder();
        if (children.length == 3 && ConstantAttributes.childIsConstant(context, 1)) {
            ConstantValue value = ConstantAttributes.child(context, 1);
            if (value != null) {
                builder.add(new TerminalNode(context.getCommandFactory().loadConstant(value)));
            }
        } else if (children.length == 3) {
            children[1].register(builder);
        }
        builder.add(new TerminalNode(context.getCommandFactory().returnCommand()));
        return new NormalCommandNodeRegister(builder.build(), production, children);
    }
}

