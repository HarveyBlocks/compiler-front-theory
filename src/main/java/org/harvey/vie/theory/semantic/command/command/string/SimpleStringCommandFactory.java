package org.harvey.vie.theory.semantic.command.command.string;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.factory.SimpleCommandFactory;
import org.harvey.vie.theory.semantic.function.FunctionRecord;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:13
 */
public class SimpleStringCommandFactory implements SimpleCommandFactory {
    /**
     * 函数功能：生成函数调用命令。
     * 输入：
     * - name：FunctionRecord 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */
    @Override
    public SemanticCommand callFunction(FunctionRecord name) {
        return new StringCommand("call " + name.getTableIndex());
    }

    /**
     * 函数功能：生成返回命令。
     * 输入：
     * - 无。
     * 输出：SemanticCommand 类型返回值。
     */

    @Override
    public SemanticCommand returnCommand() {
        return new StringCommand("return");
    }
}
