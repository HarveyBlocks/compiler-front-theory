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

public class CommandBuildCallback extends BuildStackContextCallback<CommandNodeRegister> implements
        ShiftReduceCallback {
    /**
     * 函数功能：创建 CommandBuildCallback 对象。
     * 输入：
     * - shiftStrategies：TokenTranslatorStrategy 类型参数。
     * - reduceStrategies：CommandTranslatorStrategy 类型参数。
     * 输出：无。
     */
    public CommandBuildCallback(
            TokenTranslatorStrategy shiftStrategies,
            CommandTranslatorStrategy reduceStrategies) {
        super(new CommandSupplier(shiftStrategies, reduceStrategies));
    }

    private static class CommandSupplier implements Supplier<CommandNodeRegister> {
        private final TokenTranslatorStrategy shiftStrategies;
        private final CommandTranslatorStrategy reduceStrategies;

        /**
         * 函数功能：创建 CommandSupplier 对象。
         * 输入：
         * - shiftStrategies：TokenTranslatorStrategy 类型参数。
         * - reduceStrategies：CommandTranslatorStrategy 类型参数。
         * 输出：无。
         */

        public CommandSupplier(
                TokenTranslatorStrategy shiftStrategies,
                CommandTranslatorStrategy reduceStrategies) {
            this.shiftStrategies = shiftStrategies;
            this.reduceStrategies = reduceStrategies;

        }

        /**
         * 函数功能：获取语义栈上下文。
         * 输入：
         * - context：ShiftReduceSemanticContext 类型参数。
         * 输出：Stack<CommandNodeRegister> 类型返回值。
         */

        @Override
        public Stack<CommandNodeRegister> getStackContext(ShiftReduceSemanticContext context) {
            return context.getCommandContext();
        }

        /**
         * 函数功能：创建子节点数组。
         * 输入：
         * - n：int 类型参数。
         * 输出：CommandNodeRegister[] 类型数组。
         */

        @Override
        public CommandNodeRegister[] instanceChildrenArray(int n) {
            return new CommandNodeRegister[n];
        }

        /**
         * 函数功能：在规约时创建语法树节点。
         * 输入：
         * - context：ShiftReduceSemanticContext 类型参数。
         * - production：SimpleGrammarProduction 类型参数。
         * - children：CommandNodeRegister[] 类型参数。
         * 输出：CommandNodeRegister 类型返回值。
         */

        @Override
        public CommandNodeRegister instanceNodeOnReduce(
                ShiftReduceSemanticContext context,
                SimpleGrammarProduction production,
                CommandNodeRegister[] children) {
            CommandTranslator translator = reduceStrategies.get(production);
            return translator.translate(context, production, children);
        }

        /**
         * 函数功能：在移进时创建语法树节点。
         * 输入：
         * - context：ShiftReduceSemanticContext 类型参数。
         * - token：SourceToken 类型参数。
         * 输出：CommandNodeRegister 类型返回值。
         */


        @Override
        public CommandNodeRegister instanceNodeOnShift(
                ShiftReduceSemanticContext context, SourceToken token) {
            TokenTranslator tokenTranslator = shiftStrategies.get(token.getType());
            return tokenTranslator.translate(context, token);
        }
    }

}
