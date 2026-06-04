package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.function.FunctionRecord;

/**
 * TODO
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:12
 */
public interface SimpleCommandFactory {
    /**
     * 函数功能：生成函数调用命令。
     * 输入：
     * - name：FunctionRecord 类型参数。
     * 输出：SemanticCommand 类型返回值。
     */
    SemanticCommand callFunction(FunctionRecord name);

    /**
     * 函数功能：生成返回命令。
     * 输入：
     * - 无。
     * 输出：SemanticCommand 类型返回值。
     */

    SemanticCommand returnCommand();
}
