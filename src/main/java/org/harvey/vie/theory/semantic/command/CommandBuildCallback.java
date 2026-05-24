package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.lexical.analysis.token.SourceToken;
import org.harvey.vie.theory.semantic.callback.bu.BuildStackContextCallback;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.command.translator.CommandTranslatorStrategy;
import org.harvey.vie.theory.semantic.command.translator.TokenTranslatorStrategy;
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
public class CommandBuildCallback extends BuildStackContextCallback<CommandNodeRegister> implements
        ShiftReduceCallback {
    public CommandBuildCallback(
            TokenTranslatorStrategy shiftStrategies,
            CommandTranslatorStrategy reduceStrategies) {
        super(new CommandSupplier(shiftStrategies, reduceStrategies));
    }

    private static class CommandSupplier implements Supplier<CommandNodeRegister> {
        private final TokenTranslatorStrategy shiftStrategies;
        private final CommandTranslatorStrategy reduceStrategies;

        public CommandSupplier(
                TokenTranslatorStrategy shiftStrategies,
                CommandTranslatorStrategy reduceStrategies) {
            this.shiftStrategies = shiftStrategies;
            this.reduceStrategies = reduceStrategies;

        }

        @Override
        public Stack<CommandNodeRegister> getStackContext(ShiftReduceSemanticContext context) {
            return context.getCommandContext();
        }

        @Override
        public CommandNodeRegister[] instanceChildrenArray(int n) {
            return new CommandNodeRegister[n];
        }

        @Override
        public CommandNodeRegister instanceNodeOnReduce(
                ShiftReduceSemanticContext context,
                SimpleGrammarProduction production,
                CommandNodeRegister[] children) {
            CommandTranslator translator = reduceStrategies.get(production);
            return translator.translate(context, production, children);
        }


        @Override
        public CommandNodeRegister instanceNodeOnShift(
                ShiftReduceSemanticContext context, SourceToken token) {
            TokenTranslator tokenTranslator = shiftStrategies.get(token.getType());
            return tokenTranslator.translate(context, token);
        }
    }

}
