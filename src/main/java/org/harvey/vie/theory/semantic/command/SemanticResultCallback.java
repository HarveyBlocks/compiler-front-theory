package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.exception.CompilerException;
import org.harvey.vie.theory.semantic.callback.bu.ShiftReduceCallback;
import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.node.CommandContext;
import org.harvey.vie.theory.semantic.command.register.CommandNodeRegister;
import org.harvey.vie.theory.semantic.context.SemanticAnalysisResult;
import org.harvey.vie.theory.semantic.context.ShiftReduceSemanticContext;
import org.harvey.vie.theory.syntax.grammar.produce.SimpleGrammarProduction;

import java.util.List;

/**
 * 讲解主线第 8 站：把线性中间代码封装成语义分析结果。
 * <p>
 * 在 LR 分析 accept 前，语法上已经确认整份程序可以归约到开始符号。此时
 * {@link CommandBuildCallback} 维护的
 * {@link org.harvey.vie.theory.semantic.command.node.CommandContext} 栈顶就是整个程序入口段的
 * {@link CommandNodeRegister}。本回调用 {@link CommandSegmentSupport#flatten(CommandNodeRegister)}
 * 把入口命令展开成线性中间代码。
 * <p>
 * 函数定义不应该混进入口段，所以函数体命令由函数支路提前登记到
 * {@link ShiftReduceSemanticContext#getFunctionCommandSegmentContext()}。本站把入口段、函数段、
 * 结构体表和符号表一起封装进 {@link SemanticAnalysisResult}。
 * <p>
 * 主线下一站：{@link SemanticAnalysisResult}。下一站会讲这个结果对象如何保存入口命令段、函数命令段，
 * 以及 {@link SemanticAnalysisResult#getCommands()} 如何把入口段交给打印器。
 *
 * @author Temper
 */
public class SemanticResultCallback implements ShiftReduceCallback {
    @Override
    public void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        CommandContext commandContext = context.getCommandContext();
        if (commandContext.isEmpty()) {
            throw new CompilerException("illegal statement before accept on production.");
        }
        context.setResult(buildResult(context, commandContext));
        ShiftReduceCallback.super.beforeAccept(context, production);
    }

    private static SemanticAnalysisResult buildResult(ShiftReduceSemanticContext context, CommandContext commandContext) {
        if (commandContext.isEmpty()) {
            throw new CompilerException("illegal statement before accept on production.");
        }
        CommandNodeRegister top = commandContext.peek();
        List<SemanticCommand> entryCommands = CommandSegmentSupport.flatten(top);
        if (entryCommands.isEmpty() && context.getFunctionCommandSegmentContext().isEmpty()) {
            throw new CompilerException("semantic result must contain entry commands or function commands.");
        }
        return new SemanticAnalysisResult(
                entryCommands,
                context.getFunctionCommandSegmentContext().snapshot(),
                context.structRecords(),
                context.identifierRecords()
        );
    }
}
