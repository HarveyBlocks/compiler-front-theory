package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.CommandContext;
import org.harvey.vie.theory.semantic.command.node.CommandNode;
import org.harvey.vie.theory.semantic.command.node.CommandNodeBuilder;
import org.harvey.vie.theory.semantic.command.node.CommandNodeListBuilder;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.context.SemanticAnalysisResult;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.ArrayList;
import java.util.List;

public class SemanticResultCallback implements ShiftReduceCallback {
    @Override
    public void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        CommandNodeRegister top = topCommandNodeRegister(context);
        ShiftReduceCallback.super.beforeAccept(context, production);
        context.setResult(buildResult(context, top));
    }

    private static CommandNodeRegister topCommandNodeRegister(ShiftReduceSemanticContext context) {
        CommandContext commandContext = context.getCommandContext();
        if (commandContext.size() != 1) {
            throw new CompilerException("illegal statement before accept on production.");
        }
        return commandContext.peek();
    }

    private static SemanticAnalysisResult buildResult(ShiftReduceSemanticContext context, CommandNodeRegister top) {
        CommandNodeBuilder resultBuilder = new CommandNodeListBuilder();
        top.register(resultBuilder);
        CommandNode[] array = resultBuilder.build();
        if (array.length != 1) {
            throw new CompilerException("illegal statement before accept on production.");
        }
        List<SemanticCommand> commands = new ArrayList<>();
        array[0].flat(commands);
        ThreeAddressCodePrinter printer = new ThreeAddressCodePrinter();
        List<String> lines = printer.print(commands);
        return new SemanticAnalysisResult(lines, context.identifierRecords());
    }
}
