package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.function.FunctionRecord;

import java.util.List;

/**
 * 函数命令段：把一个 {@link FunctionRecord} 和它的线性 {@link SemanticCommand} 列表绑定在一起。
 * <p>
 * {@link org.harvey.vie.theory.semantic.command.translator.command.FunctionDefinitionTranslator}
 * 会把函数体单独展开成此对象，避免函数体命令混入程序入口段。
 * 最终由 {@link org.harvey.vie.theory.semantic.context.SemanticAnalysisResult#getFunctionSegments()} 暴露给测试和报告。
 *
 * @author Temper
 */
public class FunctionCommandSegment {
    private final FunctionRecord function;
    private final List<SemanticCommand> commands;

    public FunctionCommandSegment(FunctionRecord function, List<SemanticCommand> commands) {
        this.function = function;
        this.commands = List.copyOf(commands);
    }

    public FunctionRecord getFunction() {
        return function;
    }

    public List<SemanticCommand> getCommands() {
        return commands;
    }
}
