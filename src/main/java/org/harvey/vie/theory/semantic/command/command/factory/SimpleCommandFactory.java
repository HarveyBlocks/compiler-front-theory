package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.function.FunctionRecord;

/**
 * 不依赖数据类型的命令工厂。
 * <p>
 * 函数调用和 return 不需要 {@link CommandDataType} 后缀，所以从
 * {@link TypedCommandFactory} 中分离出来。当前文本实现见
 * {@link org.harvey.vie.theory.semantic.command.command.string.SimpleStringCommandFactory}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:12
 */
public interface SimpleCommandFactory {
    SemanticCommand callFunction(FunctionRecord name);

    SemanticCommand returnCommand();
}
