package org.harvey.vie.theory.semantic.command.command.factory;

import org.harvey.vie.theory.semantic.command.command.SemanticCommand;
import org.harvey.vie.theory.semantic.command.command.SemanticLabel;
import org.harvey.vie.theory.semantic.command.command.UncertainLabelGotoCommand;
import org.harvey.vie.theory.semantic.structure.StructRecord;
import org.harvey.vie.theory.semantic.command.translator.command.OperatorFactor;
import org.harvey.vie.theory.semantic.function.FunctionRecord;
import org.harvey.vie.theory.semantic.identifier.table.IdentifierRecord;
import org.harvey.vie.theory.semantic.value.ConstantValue;

/**
 * 讲解主线第 6 站：中间命令工厂接口。
 * <p>
 * 本项目实际生成的是 {@link SemanticCommand} 对象，而不是 JVM 字节码。命令工厂把“语义动作”
 * 统一翻译成具体命令对象：加载变量地址、加载常量、数组/结构体创建、栈顶运算、类型转换、赋值写回、
 * 条件跳转、无条件跳转、函数调用和 return。
 * <p>
 * 当前 demo 使用 {@link DefaultCommandFactory} 组合
 * {@link org.harvey.vie.theory.semantic.command.command.string.TypedStringCommandFactory}
 * 与 {@link org.harvey.vie.theory.semantic.command.command.string.SimpleStringCommandFactory}，
 * 也就是把命令编码成可读字符串。讲完本接口继续看 {@link DefaultCommandFactory}，
 * 然后看具体文本实现
 * {@link org.harvey.vie.theory.semantic.command.command.string.TypedStringCommandFactory}。
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-05-24 23:16
 */
public interface CommandFactory {
    SemanticCommand loadStatic(org.harvey.vie.theory.lexical.analysis.token.SourceToken token);

    SemanticCommand loadIdentifierAddress(IdentifierRecord record);

    SemanticCommand loadConstant(ConstantValue constantValue);

    SemanticCommand newStruct(StructRecord record);

    SemanticCommand newArray(CommandDataType elementType, int totalDimensions, int specifiedDimensions);

    SemanticCommand stOperator(OperatorFactor operatorFactor, CommandDataType operandType);

    SemanticCommand stTopAddrToVal(CommandDataType type);

    SemanticCommand stTopRefToVal(CommandDataType type);

    SemanticCommand assignFromStTopToAddr(CommandDataType type);

    SemanticCommand assignFromStTopToRef(CommandDataType type);

    SemanticCommand biasFromStTopToAddr(CommandDataType elementType);

    SemanticCommand biasFromStTopToRef(CommandDataType elementType);

    SemanticCommand biasFromStTopToRef(CommandDataType fieldType, int offset);

    SemanticCommand stTopCast(CommandDataType from, CommandDataType to);

    SemanticCommand ifGoto(SemanticLabel label);

    SemanticCommand ifnGoto(SemanticLabel label);

    SemanticCommand gotoCommand(SemanticLabel label);

    UncertainLabelGotoCommand gotoCommandUncertainLabel(org.harvey.vie.theory.lexical.analysis.token.SourceToken token);

    SemanticCommand callFunction(FunctionRecord name);

    SemanticCommand returnCommand();
}
