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
import org.harvey.vie.theory.semantic.tag.TagStrategyCompose;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.Stack;

/**
 * 讲解主线第 1 站：语法分析器每发生一次移进或规约，都会通过这个回调把语法事件同步翻译成
 * {@link CommandNodeRegister}。
 * <p>
 * 这里没有生成 JVM 字节码，也没有写二进制指令文件；本模块生成的是一组可展开的
 * {@link org.harvey.vie.theory.semantic.command.command.SemanticCommand} 对象，最后由
 * {@link ThreeAddressCodePrinter} 打印成类似 三地址码/四元式的文本命令，例如
 * {@code load_st_int32_address 0}、{@code st_plus_int32}、{@code ifn_goto 32}。
 * <p>
 * 可以这样说：移进 token 时走 {@link TokenTranslatorStrategy}，规约产生式时走
 * {@link CommandTranslatorStrategy}；两类策略都返回 {@link CommandNodeRegister}，这些注册器先保留
 * “命令树”和未绑定的 {@code break}/{@code continue}，等最外层接受时再展开成线性命令。
 * <p>
 * 下一站看策略如何按语义标签选择具体翻译器：{@link TagStrategyCompose}。如果要直接看注册器如何承接翻译结果，
 * 跳到 {@link CommandNodeRegister}。
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
            // 命令生成和语法分析共用同一个移进-规约节奏，但命令结果单独压在 commandContext 里。
            // 语法栈负责状态转移，commandContext 负责保存每个 token/产生式对应的命令片段。
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
            // 规约时，先用产生式上的语义标签在 {@link TagStrategyCompose} 里找翻译器；
            // children 保持语法右部顺序，翻译器据此拼接、插入跳转、绑定标签或做类型相关命令。
            CommandTranslator translator = reduceStrategies.get(production);
            return translator.translate(context, production, children);
        }


        @Override
        public CommandNodeRegister instanceNodeOnShift(
                ShiftReduceSemanticContext context, SourceToken token) {
            // 移进时只处理单个 token。常量、break、continue 会立即产生命令；普通标识符和类型 token
            // 先占位，等对应产生式规约且符号表/类型信息已经准备好时再生成真实命令。
            TokenTranslator tokenTranslator = shiftStrategies.get(token.getType());
            return tokenTranslator.translate(context, token);
        }
    }

}
