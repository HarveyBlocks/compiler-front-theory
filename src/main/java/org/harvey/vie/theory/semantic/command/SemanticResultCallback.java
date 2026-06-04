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
 * @author Temper
 */
public class SemanticResultCallback implements ShiftReduceCallback {
    /**
     * 函数功能：处理接受前事件。
     * 输入：
     * - context：ShiftReduceSemanticContext 类型参数。
     * - production：SimpleGrammarProduction 类型参数。
     * 输出：无。
     */
    @Override
    public void beforeAccept(ShiftReduceSemanticContext context, SimpleGrammarProduction production) {
        CommandContext commandContext = context.getCommandContext();
        if (commandContext.isEmpty()) {
            throw new CompilerException("illegal statement before accept on production.");
        }
        context.setResult(buildResult(context, commandContext));
        ShiftReduceCallback.super.beforeAccept(context, production);
    }
/**
 * 函数功能：构建语义分析结果。
 * 输入：
 * - context：ShiftReduceSemanticContext 类型参数。
 * - commandContext：CommandContext 类型参数。
 * 输出：SemanticAnalysisResult 类型返回值。
 */

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

