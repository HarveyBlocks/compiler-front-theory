package org.harvey.vie.theory.semantic.command;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.function.FunctionRecord;

import java.util.List;

/**
 * @author Temper
 */
public class FunctionCommandSegment {
    private final FunctionRecord function;
    private final List<SemanticCommand> commands;
/**
 * 函数功能：创建 FunctionCommandSegment 对象。
 * 输入：
 * - function：FunctionRecord 类型参数。
 * - commands：List<SemanticCommand> 类型参数。
 * 输出：无。
 */

    public FunctionCommandSegment(FunctionRecord function, List<SemanticCommand> commands) {
        this.function = function;
        this.commands = List.copyOf(commands);
    }
/**
 * 函数功能：获取函数记录。
 * 输入：
 * - 无。
 * 输出：FunctionRecord 类型返回值。
 */

    public FunctionRecord getFunction() {
        return function;
    }
/**
 * 函数功能：获取命令列表。
 * 输入：
 * - 无。
 * 输出：List<SemanticCommand> 类型集合或迭代结果。
 */

    public List<SemanticCommand> getCommands() {
        return commands;
    }
}
