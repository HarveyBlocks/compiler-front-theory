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
 * 讲解主线第 8 站：在语法分析接受前，把命令生成结果封装为 {@link SemanticAnalysisResult}。
 * <p>
 * {@link CommandBuildCallback} 在移进/规约过程中一直维护
 * {@link org.harvey.vie.theory.semantic.command.node.CommandContext}。到 accept 前，栈顶就是整个程序入口段的
 * {@link CommandNodeRegister}。本回调用 {@link CommandSegmentSupport#flatten(CommandNodeRegister)}
 * 把入口命令展开，同时从
 * {@link ShiftReduceSemanticContext#getFunctionCommandSegmentContext()} 取出函数定义支路登记的函数段。
 * <p>
 * 讲完本类继续看 {@link SemanticAnalysisResult#getCommands()} 和
 * {@link ThreeAddressCodePrinter}，那里是测试和报告真正读取命令文本的入口。
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
