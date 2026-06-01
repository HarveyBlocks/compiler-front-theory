package org.harvey.vie.theory.semantic.command.command.string;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.factory.SimpleCommandFactory;
import org.harvey.vie.theory.semantic.function.FunctionRecord;

/**
 * 简单文本命令工厂：生成不带数据类型后缀的命令。
 * <p>
 * {@code call n} 使用函数表下标定位被调函数；{@code return} 表示从当前函数段返回。
 * 讲完本类回到 {@link org.harvey.vie.theory.semantic.command.command.factory.DefaultCommandFactory}，
 * 或看带类型命令工厂 {@link TypedStringCommandFactory}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:13
 */
public class SimpleStringCommandFactory implements SimpleCommandFactory {
    @Override
    public SemanticCommand callFunction(FunctionRecord name) {
        return new StringCommand("call " + name.getTableIndex());
    }

    @Override
    public SemanticCommand returnCommand() {
        return new StringCommand("return");
    }
}
