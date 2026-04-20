package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;
import org.harvey.vie.theory.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-20 22:43
 */
public class SemanticCommandPrintCallback implements ShiftReduceCallback {

    @Override
    public void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        CommandContext.CommandNodeRegister top = topCommandNodeRegister(context);
        ShiftReduceCallback.super.beforeAccept(context, production);
        printResult(top);
    }

    private static CommandContext.CommandNodeRegister topCommandNodeRegister(ShiftReduceSemanticContext context) {
        CommandContext commandContext = context.getCommandContext();
        if (commandContext.size() != 1) {
            // 由于增广语法 S' -> S, 右部只有一个
            throw new CompilerException("不正确的状态: ");
        }
        return commandContext.peek();
    }

    private static void printResult(CommandContext.CommandNodeRegister top) {
        CommandNodeListBuilder resultBuilder = new CommandNodeListBuilder();
        top.register(resultBuilder);
        CommandContext.CommandNode[] array = resultBuilder.toArray();
        if (array.length != 1) {
            // 由于增广语法 S' -> S, 右部只有一个
            throw new CompilerException("不正确的状态: ");
        }
        // 右部
        CommandContext.CommandNode programNode = array[0];
        List<SemanticCommand> result = new ArrayList<>();
        programNode.flat(result);
        System.out.println("command result: ");
        IdGenerator rawGenerator = new IdGenerator();
        for (SemanticCommand semanticCommand : result) {
            System.out.printf("[%03d] %s\n", rawGenerator.next(), semanticCommand);
        }
    }

}
