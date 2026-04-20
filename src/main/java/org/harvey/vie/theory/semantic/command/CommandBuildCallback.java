package org.harvey.vie.theory.semantic.command;

import lombok.AllArgsConstructor;
import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.BuildStackContextCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.command.translator.command.CommandTranslator;
import org.harvey.vie.theory.semantic.command.translator.token.TokenTranslator;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.Stack;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-19 19:10
 */
public class CommandBuildCallback extends BuildStackContextCallback<CommandContext.CommandNodeRegister> implements
        ShiftReduceCallback {
    public CommandBuildCallback(
            CommandContext.TokenTranslatorStrategy shiftStrategies,
            CommandContext.CommandTranslatorStrategy reduceStrategies) {
        super(new CommandSupplier(shiftStrategies, reduceStrategies), new CommandVisitor());
    }

    private static class CommandSupplier implements Supplier<CommandContext.CommandNodeRegister> {
        private final CommandContext.TokenTranslatorStrategy shiftStrategies;
        private final CommandContext.CommandTranslatorStrategy reduceStrategies;

        public CommandSupplier(
                CommandContext.TokenTranslatorStrategy shiftStrategies,
                CommandContext.CommandTranslatorStrategy reduceStrategies) {
            this.shiftStrategies = shiftStrategies;
            this.reduceStrategies = reduceStrategies;

        }

        @Override
        public Stack<CommandContext.CommandNodeRegister> getStackContext(ShiftReduceSemanticContext context) {
            return context.getCommandContext();
        }

        @Override
        public CommandContext.CommandNodeRegister[] instanceChildrenArray(int n) {
            return new CommandContext.CommandNodeRegister[n];
        }

        @Override
        public CommandContext.CommandNodeRegister instanceNodeOnReduce(
                ShiftReduceSemanticContext context,
                SimpleGrammarProduction production,
                CommandContext.CommandNodeRegister[] children) {
            int productionId = context.getSyntaxContext().getProductionId(production);
            CommandTranslator translator = reduceStrategies.get(productionId);
            return translator.translate(context, production, children);
        }

        @Override
        public CommandContext.CommandNodeRegister instanceNodeOnShift(
                ShiftReduceSemanticContext context, SourceToken token) {
            TokenTranslator tokenTranslator = shiftStrategies.get(token.getType());
            return tokenTranslator.translate(context, token);
        }
    }

    @AllArgsConstructor
    private static class CommandVisitor implements Visitor<CommandContext.CommandNodeRegister> {
    }
}